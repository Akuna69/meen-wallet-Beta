package io.meen.apollo.domain

import android.content.Context
import io.meen.apollo.data.afs.MetricsProvider
import io.meen.apollo.data.os.GooglePlayHelper
import io.meen.apollo.data.os.GooglePlayServicesHelper
import io.meen.apollo.data.preferences.FirebaseInstallationIdRepository
import io.meen.apollo.domain.model.report.ErrorReport
import io.meen.apollo.domain.model.report.EmailReport
import io.meen.apollo.domain.model.user.User
import io.meen.apollo.domain.selector.UserSelector
import io.meen.apollo.domain.utils.locale
import io.meen.common.utils.Encodings
import io.meen.common.utils.Hashes
import timber.log.Timber
import javax.inject.Inject

class EmailReportManager @Inject constructor(
    private val userSel: UserSelector,
    private val googlePlayServicesHelper: GooglePlayServicesHelper,
    private val googlePlayHelper: GooglePlayHelper,
    private val firebaseInstallationIdRepo: FirebaseInstallationIdRepository,
    private val context: Context,
    private val metricsProvider: MetricsProvider,
) {

    fun buildAbridgedEmailReport(report: ErrorReport, presenterName: String): EmailReport {
        return buildEmailReport(report, presenterName, abridged = true)
    }

    fun buildEmailReport(
        report: ErrorReport,
        presenterName: String,
        abridged: Boolean = false,
    ): EmailReport {

        val supportId = userSel.getOptional()
            .flatMap { obj: User -> obj.supportId }
            .orElse(null)

        return EmailReport.Builder()
            .report(report)
            .supportId(supportId)
            .bigQueryPseudoId(firebaseInstallationIdRepo.getBigQueryPseudoId())
            .fcmTokenHash(getFcmTokenHash())
            .presenterName(presenterName)
            .googlePlayServices(googlePlayServicesHelper.isAvailable)
            .googlePlayServicesVersionCode(googlePlayServicesHelper.versionCode)
            .googlePlayServicesVersionName(googlePlayServicesHelper.versionName)
            .googlePlayServicesClientVersionCode(googlePlayServicesHelper.clientVersionCode)
            .googlePlayVersionCode(googlePlayHelper.versionCode)
            .googlePlayVersionName(googlePlayHelper.versionName)
            .defaultRegion(metricsProvider.telephonyNetworkRegion.orElse("null"))
            .rootHint(metricsProvider.isRootHint)
            .locale(context.locale())
            .isLowRamDevice(metricsProvider.isLowRamDevice)
            .isBackgroundRestricted(metricsProvider.isBackgroundRestricted)
            .isLowMemoryKillReportSupported(metricsProvider.isLowMemoryKillReportSupported)
            .exitReasons(metricsProvider.exitReasons)
            .deviceName(metricsProvider.deviceName)
            .deviceModel(metricsProvider.deviceModel)
            .deviceManufacturer(metricsProvider.buildInfo.manufacturer)
            .build(abridged)
    }

    private fun getFcmTokenHash() = try {
        val fcmToken: String? = firebaseInstallationIdRepo.getFcmToken()

        if (fcmToken != null) {
            Encodings.bytesToHex(Hashes.sha256(Encodings.stringToBytes(fcmToken)))

        } else {
            "null"
        }
    } catch (e: Throwable) {  // Avoid crash, we're already processing an error (report).
        Timber.e(e)
        "unavailable"
    }
}