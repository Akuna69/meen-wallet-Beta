package io.meen.apollo.domain.action.ek

import io.meen.apollo.data.apis.DriveFile
import io.meen.apollo.data.apis.DriveUploader
import io.meen.apollo.data.fs.LocalFile
import io.meen.apollo.data.preferences.UserRepository
import io.meen.apollo.domain.action.base.BaseAsyncAction2
import io.meen.apollo.domain.model.EmergencyKitExport
import io.meen.apollo.domain.model.GeneratedEmergencyKitInfo
import io.meen.apollo.domain.model.user.User
import io.meen.common.utils.Encodings
import io.meen.common.utils.Hashes
import rx.Observable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UploadToDriveAction @Inject constructor(
    private val userRepository: UserRepository,
    private val driveUploader: DriveUploader,
    private val reportEmergencyKitExported: ReportEmergencyKitExportedAction,
): BaseAsyncAction2<LocalFile, GeneratedEmergencyKitInfo, DriveFile>() {

    companion object {
        val PROP_USER = "meen_user"
        val PROP_EK_VERSION = "meen_ek_version"
    }

    /**
     * Upload a file to Google Drive, assuming an account is signed in.
     */
    override fun action(localFile: LocalFile, ek: GeneratedEmergencyKitInfo): Observable<DriveFile> =
        Observable
            .defer { userRepository.fetch() }
            .first()
            .flatMap {
                val props = mapOf(
                    PROP_USER to getUserPropValue(it),
                    PROP_EK_VERSION to getEkVersionValue(ek)
                )

                driveUploader.upload(localFile.toFile(), localFile.type, PROP_USER, props)
            }
            .flatMap { driveFile ->
                reportEmergencyKitExported.actionNow(
                    EmergencyKitExport(
                        ek,
                        true,
                        EmergencyKitExport.Method.DRIVE
                    )
                )

                Observable.just(driveFile)
            }


    /** Get the PROP_USER value, which is the hex-encoded hash of the stringified Houston ID */
    private fun getUserPropValue(user: User): String {
        val hidBytes = user.hid.toString().toByteArray(Charsets.UTF_8)
        val hidHash = Hashes.sha256(hidBytes)

        return Encodings.bytesToHex(hidHash)
    }

    /** Get the PROP_EK_VERSION value, which is the stringified version number */
    private fun getEkVersionValue(ek: GeneratedEmergencyKitInfo): String {
        return ek.version.toString()
    }
}
