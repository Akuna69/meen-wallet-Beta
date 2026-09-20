package io.meen.apollo.data.net.base.interceptor

import io.meen.apollo.data.afs.BackgroundExecutionMetricsProvider
import io.meen.apollo.data.net.base.BaseInterceptor
import io.meen.apollo.data.toSafeAscii
import io.meen.apollo.domain.errors.data.MeenSerializationError
import io.meen.apollo.domain.model.user.User
import io.meen.apollo.domain.selector.UserSelector
import io.meen.common.net.HeaderUtils
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.Request
import timber.log.Timber
import javax.inject.Inject

class BackgroundExecutionMetricsInterceptor @Inject constructor(
    private val bemProvider: BackgroundExecutionMetricsProvider,
    private val userSel: UserSelector,
) : BaseInterceptor() {

    override fun processRequest(originalRequest: Request): Request {
        return originalRequest.newBuilder()
            .addBem(originalRequest)
            .build()
    }

    private fun Request.Builder.addBem(originalRequest: Request): Request.Builder {
        val encodeToJson = safelyEncodeJson(originalRequest)

        return if (encodeToJson != null) {
            try {
                addHeader(HeaderUtils.BACKGROUND_EXECUTION_METRICS, encodeToJson)
            } catch (e: Throwable) {
                logError(originalRequest, e)
                this
            }
        } else {
            this
        }
    }

    private fun safelyEncodeJson(originalRequest: Request): String? {
        return try {
            Json.encodeToString(bemProvider.run()).toSafeAscii()
        } catch (e: Throwable) {
            logError(originalRequest, e)
            null
        }
    }

    private fun logError(originalRequest: Request, e: Throwable) {
        val supportId = userSel.getOptional()
            .flatMap { obj: User -> obj.supportId }
            .orElse("Not logged in")
        Timber.e(MeenSerializationError(supportId, originalRequest, e))
    }
}