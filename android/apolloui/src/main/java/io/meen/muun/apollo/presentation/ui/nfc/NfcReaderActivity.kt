package io.meen.apollo.presentation.ui.nfc

import android.content.Context
import android.content.Intent
import android.nfc.NfcAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.widget.TextView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewbinding.ViewBinding
import io.meen.apollo.R
import io.meen.apollo.data.nfc.api.NfcSession
import io.meen.apollo.databinding.NfcReaderActivityBinding
import io.meen.apollo.domain.analytics.AnalyticsEvent
import io.meen.apollo.domain.model.FeasibleZone
import io.meen.apollo.presentation.ui.activity.extension.MeenDialog
import io.meen.apollo.presentation.ui.activity.extension.NfcReaderModeExtension
import io.meen.apollo.presentation.ui.base.BasePresenter
import io.meen.apollo.presentation.ui.base.BaseView
import io.meen.apollo.presentation.ui.base.SingleFragmentActivity
import io.meen.apollo.presentation.ui.fragments.error.ErrorFragmentDelegate
import io.meen.apollo.presentation.ui.fragments.error.ErrorViewModel
import io.meen.apollo.presentation.ui.nfc.NfcReaderViewModel.ViewCommand
import io.meen.apollo.presentation.ui.utils.attachChildAtMm
import io.meen.apollo.presentation.ui.utils.vibrateShort
import io.meen.apollo.presentation.ui.view.MeenHeader
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class NfcReaderActivity : SingleFragmentActivity<BasePresenter<BaseView>>(), ErrorFragmentDelegate {

    companion object {
        fun getStartActivityIntent(context: Context) =
            Intent(context, NfcReaderActivity::class.java)
    }

    @Inject
    lateinit var viewModel: NfcReaderViewModel

    @Inject
    lateinit var nfcReaderModeExtension: NfcReaderModeExtension

    override fun inject() {
        component.inject(this)
    }

    override fun setUpExtensions() {
        super.setUpExtensions()
        addExtension(nfcReaderModeExtension)
    }

    private val binding: NfcReaderActivityBinding
        get() = getBinding() as NfcReaderActivityBinding

    override fun bindingInflater(): (LayoutInflater) -> ViewBinding {
        return NfcReaderActivityBinding::inflate
    }

    override fun getLayoutResource() = R.layout.nfc_reader_activity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initializeUi()
        generateAppEvent("s_nfc_reader")
        // TODO: this fetch is going to be moved outside the screen and should be removed from here
        loadBoundaryData()

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.viewCommand.collect { viewCommand ->
                        when (viewCommand) {
                            is ViewCommand.Success -> {
                                generateAppEvent("nfc_reading_success")
                                vibrateShort()
                                disableReaderMode()
                                viewModel.securityCard2faSuccess(this@NfcReaderActivity)
                            }

                            is ViewCommand.Error -> {
                                handleNfcError(viewCommand)
                            }

                            is ViewCommand.DisableSecurityCardFlag -> {
                                handleDisableSecurityCardFlag()
                            }
                        }
                    }
                }
                launch {
                    viewModel.viewState.collectLatest { viewState ->
                        when (viewState) {
                            is NfcReaderViewModel.ViewState.Reading -> {
                                Timber.i("Reading nfc card")
                            }

                            is NfcReaderViewModel.ViewState.Scanning -> {
                                viewState.feasibleZone?.let {
                                    setBoundaryData(it)
                                }
                                Timber.i("Scanning for card")
                            }
                        }
                    }
                }
            }
        }
    }

    private fun handleNfcError(error: ViewCommand.Error) {
        generateAppEvent("nfc_reading_error")

        Timber.e("NFC Error: ${error.message}")

        showError(
            ErrorViewModel.Builder()
                .loggingName(AnalyticsEvent.ERROR_TYPE.NFC_2FA_FAILED)
                .kind(ErrorViewModel.ErrorViewKind.REPORTABLE)
                // TODO get proper texts
                .title("Error Reading Security Card")
                .description(error.message)
                .canGoBack(true)
                .build()
        )
    }

    private fun handleDisableSecurityCardFlag() {
        MeenDialog.Builder()
            .title(R.string.nfc_reader_screen_disable_ff_title)
            .message(R.string.nfc_reader_screen_disable_ff_desc)
            .positiveButton(R.string.nfc_reader_screen_disable_ff_yes) {
                viewModel.disableSecurityCardFF()
                finishActivity()
            }
            .negativeButton(R.string.no) {
                viewModel.cancelDisableSecurityCardFF()
            }
            .build()
            .let(::showDialog)
    }

    override fun onResume() {
        super.onResume()

        viewModel.subscribeToAllSensors(this, this)

        if (!isNfcEnabled()) {
            generateAppEvent("nfc_disabled_dialog")
            MeenDialog.Builder()
                .title(R.string.nfc_reader_screen_title)
                .message(R.string.nfc_reader_screen_enable_nfc)
                .positiveButton(R.string.nfc_reader_screen_go_to_configs) {
                    startActivity(Intent(android.provider.Settings.ACTION_NFC_SETTINGS))
                }
                .negativeButton(R.string.cancel) {
                    finishActivity()
                }
                .build()
                .let(::showDialog)
        }

        enableReaderMode()
    }

    override fun onPause() {
        super.onPause()

        viewModel.unsubscribeFromAllSensors()

        disableReaderMode()
    }

    override fun initializeUi() {
        with(binding.header) {
            attachToActivity(this@NfcReaderActivity)
            setNavigation(MeenHeader.Navigation.BACK)
            // TODO: define this title or if we're going to need a screen title here
            showTitle(R.string.nfc_reader_screen_title)
            setElevated(false)
        }
        val container = binding.container
        val nfcSensorView = NfcSensorView(container.context)

        viewModel.getAntennaPosition()?.let {
            container.attachChildAtMm(
                childView = nfcSensorView,
                originXmm = it.first,
                originYmm = it.second,
                modifications = { pxPos ->
                    pxPos * 0.5f // -50% shift for positioning in the middle
                },
            )
        } ?: run {
            // TODO: get this information from table if not API > 34
            val textView = TextView(this).apply {
                setText(R.string.nfc_reader_screen_unknown_coordinates)
            }
            container.attachChildAtMm(
                childView = textView,
            )
        }
    }

    override fun getHeader(): MeenHeader {
        return binding.header
    }

    private fun loadBoundaryData() {
        viewModel.fetchFeasibleZoneBoundary()
    }

    private fun setBoundaryData(feasibleZone: FeasibleZone) {
        binding.nfcFeasibleAreaView.apply {
            setData(feasibleZone)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        viewModel.onGestureDetected(event)
        return super.onTouchEvent(event)
    }

    private fun isNfcEnabled(): Boolean {
        val nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        return nfcAdapter?.isEnabled == true
    }

    override fun onNewNfcSession(nfcSession: NfcSession) {
        viewModel.onNewNfcSession(nfcSession)
    }

    private fun enableReaderMode() {
        nfcReaderModeExtension.enableReaderMode()
    }

    private fun disableReaderMode() {
        nfcReaderModeExtension.disableReaderMode()
    }

    private fun generateAppEvent(eventName: String) {
        viewModel.generateAppEvent(eventName)
    }

    override fun handleBack(errorType: AnalyticsEvent.ERROR_TYPE) {
        finishActivity()
    }

    override fun handleSendReport() {
        if (viewModel.latestError != null) {
            presenter.sendErrorReport(viewModel.latestError)
        }
    }
}