package io.meen.apollo.domain.action.ek

import io.meen.apollo.data.os.execution.ExecutionTransformerFactory
import io.meen.apollo.data.preferences.KeysRepository
import io.meen.apollo.data.preferences.UserRepository
import io.meen.apollo.domain.action.base.BaseAsyncAction0
import io.meen.apollo.domain.libwallet.LibwalletBridge
import io.meen.apollo.domain.model.EmergencyKitExport
import io.meen.apollo.domain.model.GeneratedEmergencyKitHTML
import io.meen.apollo.domain.utils.Trace
import io.meen.apollo.domain.utils.EK_CHILD_MEEN_FINGERPRINT
import io.meen.apollo.domain.utils.EK_CHILD_MEEN_KEY
import io.meen.apollo.domain.utils.EK_CHILD_RC_CHECKSUM
import io.meen.apollo.domain.utils.EK_CHILD_USER_FINGERPRINT
import io.meen.apollo.domain.utils.EK_CHILD_USER_KEY
import io.meen.apollo.domain.utils.TraceLabel
import io.meen.apollo.domain.utils.TimeTracker
import io.meen.common.crypto.ChallengeType
import rx.Observable
import timber.log.Timber
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RenderEmergencyKitAction @Inject constructor(
    private val userRepository: UserRepository,
    private val keysRepository: KeysRepository,
    private val reportEmergencyKitExported: ReportEmergencyKitExportedAction,
    private val transformerFactory: ExecutionTransformerFactory,
    private val getOrCreateEncryptedBasePrivateKeyAction: GetOrCreateEncryptedBasePrivateKeyAction,
    private val timeTracker: TimeTracker,
) : BaseAsyncAction0<GeneratedEmergencyKitHTML>() {

    inner class RequiredData(
        val userKey: String,
        val userFingerprint: String,
        val meenKey: String,
        val meenFingerprint: String,
        val rcChecksum: String,
    )

    var onDataFetched: (() -> Unit)? = null

    /**
     * Prepare the emergency kit for export, and render the HTML.
     */
    override fun action(): Observable<GeneratedEmergencyKitHTML> =
        Observable.defer {
            val requiredDataFetchingTrace = timeTracker.start(TraceLabel.EK_LEGACY_DATA_FETCHING)

            watchData(requiredDataFetchingTrace).first()
                .doOnNext {
                    requiredDataFetchingTrace.finish()
                    onDataFetched?.invoke()
                    onDataFetched = null
                }
                .map { renderSave(it) }
                .doOnNext { ek ->
                    val export = EmergencyKitExport(
                        ek.info,
                        false,
                        EmergencyKitExport.Method.UNKNOWN
                    )

                    // NOTE:
                    // Rather than use `run()`, we subscribe to this action() in background to avoid
                    // competing with other callers for the Action concurrency check.
                    // Remember: this is a fire-and-forget call
                    reportEmergencyKitExported.action(export)
                        .subscribeOn(transformerFactory.backgroundScheduler)
                        .subscribe({}, { error ->
                            Timber.i("Error while reportEmergencyKitExported")
                            Timber.e(error)
                        })
                }
        }

    private fun renderSave(data: RequiredData): GeneratedEmergencyKitHTML {
        val kitGen = LibwalletBridge.generateEmergencyKit(
            data.userKey,
            data.userFingerprint,
            data.meenKey,
            data.meenFingerprint,
            data.rcChecksum,
            Locale.getDefault()
        )

        userRepository.storeEmergencyKitVerificationCode(kitGen.info.verificationCode)

        return kitGen
    }

    private fun watchData(requiredDataFetchingTrace: Trace): Observable<RequiredData> {
        val tUserKey = requiredDataFetchingTrace.child(EK_CHILD_USER_KEY)
        val tUserFp = requiredDataFetchingTrace.child(EK_CHILD_USER_FINGERPRINT)
        val tMeenKey = requiredDataFetchingTrace.child(EK_CHILD_MEEN_KEY)
        val tMeenFp = requiredDataFetchingTrace.child(EK_CHILD_MEEN_FINGERPRINT)
        val tRcChecksum = requiredDataFetchingTrace.child(EK_CHILD_RC_CHECKSUM)

        val challengePublicKey = keysRepository.getChallengePublicKey(ChallengeType.RECOVERY_CODE)

        return Observable.zip(
            getEncryptedBasePrivateKey().doOnNext { tUserKey.finish() },
            keysRepository.userKeyFingerprint.doOnNext { tUserFp.finish() },
            keysRepository.encryptedMeenPrivateKey.doOnNext { tMeenKey.finish() },
            keysRepository.meenKeyFingerprint.doOnNext { tMeenFp.finish() },
            challengePublicKey.map { it.checksum }.doOnNext { tRcChecksum.finish() },
            ::RequiredData
        )
    }

    private fun getEncryptedBasePrivateKey(): Observable<String> {
        return getOrCreateEncryptedBasePrivateKeyAction.action()
    }
}
