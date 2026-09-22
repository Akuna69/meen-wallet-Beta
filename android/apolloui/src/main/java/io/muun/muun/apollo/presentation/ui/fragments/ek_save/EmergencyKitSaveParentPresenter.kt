package io.meen.apollo.presentation.ui.fragments.ek_save

import io.meen.apollo.data.apis.DriveFile
import io.meen.apollo.domain.model.GeneratedEmergencyKitInfo
import io.meen.apollo.presentation.ui.base.ParentPresenter

interface EmergencyKitSaveParentPresenter : ParentPresenter {

    fun setGeneratedEmergencyKit(kitGen: GeneratedEmergencyKitInfo)

    fun getGeneratedEmergencyKit(): GeneratedEmergencyKitInfo

    fun confirmEmergencyKitUploaded(driveFile: DriveFile)

    /**
     * There's a limitation here. Android OS doesn't allow us to know with 100% certainty that
     * the EK was successfully shared or saved locally. But this is our best effort, we accept
     * there will be false-positives. We signal that this step of the flow is completed and we can
     * move forward. Worst case, users can always press back and return.
     */
    fun confirmManualShareCompleted()

    fun cancelEmergencyKitSave()
}