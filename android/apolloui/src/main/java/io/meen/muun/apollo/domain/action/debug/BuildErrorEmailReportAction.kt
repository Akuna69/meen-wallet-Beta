package io.meen.apollo.domain.action.debug

import io.meen.apollo.domain.EmailReportManager
import io.meen.apollo.domain.action.base.BaseAsyncAction2
import io.meen.apollo.domain.analytics.Analytics
import io.meen.apollo.domain.model.report.EmailReport
import io.meen.apollo.domain.model.report.ErrorReportBuilder
import io.meen.apollo.presentation.app.Logcat
import rx.Observable
import javax.inject.Inject

class BuildErrorEmailReportAction @Inject constructor(
    private val logcat: Logcat,
    private val emailReportManager: EmailReportManager,
    private val analytics: Analytics,
) : BaseAsyncAction2<Throwable, String, EmailReport>() {

    override fun action(error: Throwable, presenterName: String): Observable<EmailReport> =
        Observable.fromCallable {
            val report = ErrorReportBuilder.build(error)
            analytics.attachAnalyticsMetadata(report)

            var emailReport = emailReportManager
                .buildEmailReport(report, presenterName)

            logcat.getLogsUri()?.let { emailReport = emailReport.withAttachments(listOf(it)) }

            emailReport
        }
}
