package io.meen.apollo.presentation.ui.select_bitcoin_unit

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import androidx.viewbinding.ViewBinding
import icepick.State
import io.meen.apollo.R
import io.meen.apollo.databinding.ActivitySelectBitcoinUnitBinding
import io.meen.apollo.domain.model.BitcoinUnit
import io.meen.apollo.presentation.ui.base.BaseActivity
import io.meen.apollo.presentation.ui.utils.UiUtils
import io.meen.apollo.presentation.ui.view.MeenHeader
import io.meen.apollo.presentation.ui.view.MeenHeader.Navigation
import io.meen.apollo.presentation.ui.view.MeenSettingItem

class SelectBitcoinUnitActivity: BaseActivity<SelectBitcoinUnitPresenter>(), SelectBitcoinUnitView {

    companion object {
        fun getStartActivityIntent(context: Context) =
            Intent(context, SelectBitcoinUnitActivity::class.java)
    }

    private val binding: ActivitySelectBitcoinUnitBinding
        get() = getBinding() as ActivitySelectBitcoinUnitBinding

    private val header: MeenHeader
        get() = binding.selectCurrencyHeader

    private val bitcoinUnitItem: MeenSettingItem
        get() = binding.bitcoinUnitBtc

    private val satoshisUnitItem: MeenSettingItem
        get() = binding.bitcoinUnitSat

    @State
    @JvmField
    var bitcoinUnit: BitcoinUnit? = null

    override fun inject() =
        component.inject(this)

    override fun getLayoutResource() =
        R.layout.activity_select_bitcoin_unit

    override fun bindingInflater(): (LayoutInflater) -> ViewBinding {
        return ActivitySelectBitcoinUnitBinding::inflate
    }

    override fun initializeUi() {
        super.initializeUi()

        header.let {
            it.attachToActivity(this)
            it.showTitle(R.string.select_bitcoin_unit_title)
            it.setNavigation(Navigation.BACK)
        }

        bitcoinUnitItem.setOnClickListener { onItemSelected(BitcoinUnit.BTC) }
        satoshisUnitItem.setOnClickListener { onItemSelected(BitcoinUnit.SATS) }

        updateSelection()
    }

    private fun onItemSelected(newBitcoinUnit: BitcoinUnit) {
        presenter.changeBitcoinUnit(newBitcoinUnit)
    }

    override fun setBitcoinUnit(bitcoinUnit: BitcoinUnit) {
        this.bitcoinUnit = bitcoinUnit
        updateSelection()
    }

    private fun updateSelection() {
        val selectedIcon = UiUtils.getTintedDrawable(
            this,
            R.drawable.ic_check_black_24_px,
            R.color.blue
        )

        satoshisUnitItem.setIcon(null)
        bitcoinUnitItem.setIcon(null)

        if (bitcoinUnit == BitcoinUnit.BTC) {
            bitcoinUnitItem.setIcon(selectedIcon)

        } else if (bitcoinUnit == BitcoinUnit.SATS) {
            satoshisUnitItem.setIcon(selectedIcon)
        }
    }
}