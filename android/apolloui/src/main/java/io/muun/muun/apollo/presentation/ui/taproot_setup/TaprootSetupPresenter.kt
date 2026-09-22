package io.meen.apollo.presentation.ui.taproot_setup

import android.os.Bundle
import io.meen.apollo.data.apis.DriveFile
import io.meen.apollo.data.apis.DriveUploader
import io.meen.apollo.domain.analytics.AnalyticsEvent.E_TAPROOT_SLIDES_ABORTED
import io.meen.apollo.domain.model.GeneratedEmergencyKitInfo
import io.meen.apollo.presentation.ui.base.BasePresenter
import io.meen.apollo.presentation.ui.base.di.PerActivity
import io.meen.apollo.presentation.ui.fragments.ek_save.EmergencyKitSaveParentPresenter
import io.meen.apollo.presentation.ui.fragments.ek_verify.EmergencyKitVerifyParentPresenter
import io.meen.apollo.presentation.ui.fragments.ek_verify_cloud.EmergencyKitCloudVerifyParentPresenter
import io.meen.apollo.presentation.ui.fragments.ek_verify_help.EmergencyKitVerifyHelpParentPresenter
import io.meen.apollo.presentation.ui.fragments.flow_intro.FlowIntroView
import io.meen.apollo.presentation.ui.fragments.tr_intro.TaprootIntroPager
import io.meen.apollo.presentation.ui.fragments.tr_intro.TaprootIntroParentPresenter
import javax.inject.Inject

@PerActivity
class TaprootSetupPresenter @Inject constructor(
    private val driveUploader: DriveUploader,
) : BasePresenter<TaprootSetupView>(),
    TaprootIntroParentPresenter,
    EmergencyKitSaveParentPresenter,
    EmergencyKitVerifyParentPresenter,
    EmergencyKitVerifyHelpParentPresenter,
    EmergencyKitCloudVerifyParentPresenter {

    var step = TaprootSetupStep.INTRO

    var uploadedFile: DriveFile? = null

    private var generatedEK: GeneratedEmergencyKitInfo? = null

    override fun setUp(arguments: Bundle) {
        super.setUp(arguments)
    }

    private fun goToStep(nextStep: TaprootSetupStep, args: Bundle = Bundle()) {
        this.step = nextStep
        view.goToStep(nextStep, args)
    }

    override fun confirmIntroduction() {
        goToStep(TaprootSetupStep.SAVE)
    }

    override fun abortIntroduction() {
        analytics.report(E_TAPROOT_SLIDES_ABORTED())
        view.finishActivity()
    }

    override fun setGeneratedEmergencyKit(kitGen: GeneratedEmergencyKitInfo) {
        generatedEK = kitGen
    }

    override fun getGeneratedEmergencyKit(): GeneratedEmergencyKitInfo =
        generatedEK!!

    override fun confirmEmergencyKitUploaded(driveFile: DriveFile) {
        this.uploadedFile = driveFile

        val shouldGoToCloudVerify = true // TODO

        if (shouldGoToCloudVerify) {
            goToStep(TaprootSetupStep.CLOUD_VERIFY)
        } else {
            goToStep(TaprootSetupStep.SUCCESS)
        }
    }

    override fun confirmManualShareCompleted() {
        goToStep(TaprootSetupStep.VERIFY)
    }

    override fun confirmEmergencyKitVerify() {
        goToStep(TaprootSetupStep.SUCCESS)
    }

    override fun confirmEmergencyKitCloudVerify() {
        goToStep(TaprootSetupStep.SUCCESS)
    }

    override fun cancelEmergencyKitCloudVerify() {
        goToStep(TaprootSetupStep.SAVE)
    }

    override fun openEmergencyKitCloudFile() {
        driveUploader.open(context, uploadedFile!!)
    }

    override fun showEmergencyKitVerifyHelp() {
        goToStep(TaprootSetupStep.VERIFY_HELP)
    }

    override fun cancelEmergencyKitVerify() {
        goToStep(TaprootSetupStep.SAVE)
    }

    override fun cancelEmergencyKitVerifyHelp() {
        goToStep(TaprootSetupStep.VERIFY)
    }

    override fun cancelEmergencyKitSave() {
        val args = Bundle()
        args.putInt(FlowIntroView.ARG_STEP, TaprootIntroPager.PAGES.size - 1)

        goToStep(TaprootSetupStep.INTRO, args)
    }

    override fun refreshToolbar() {
        // Nothing to do
    }
}