package io.meen.apollo.domain.action.ek

import io.meen.apollo.data.net.HoustonClient
import io.meen.apollo.data.preferences.UserRepository
import io.meen.apollo.domain.action.base.BaseAsyncAction1
import io.meen.apollo.domain.model.EmergencyKitExport
import io.meen.apollo.domain.model.user.EmergencyKit
import io.meen.common.Optional
import rx.Observable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReportEmergencyKitExportedAction @Inject constructor(
    private val houstonClient: HoustonClient,
    private val userRepository: UserRepository,
) : BaseAsyncAction1<EmergencyKitExport, Void>() {

    /**
     * Tell Houston we have exported our keys.
     */
    override fun action(export: EmergencyKitExport): Observable<Void> =
        Observable.defer {
            val user = userRepository.fetchOne()

            if (export.isVerified) {
                // Store locally for immediate feedback:
                val emergencyKit = EmergencyKit(
                    export.exportedAt,
                    export.getKitVersion(),
                    export.method
                )

                user.emergencyKit = Optional.of(emergencyKit)
                user.emergencyKitVersions.add(emergencyKit.version)
                userRepository.store(user)
            }

            houstonClient.reportEmergencyKitExported(export)
        }
}
