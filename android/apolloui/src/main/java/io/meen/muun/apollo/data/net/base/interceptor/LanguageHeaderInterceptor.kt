package io.meen.apollo.data.net.base.interceptor

import android.content.Context
import io.meen.apollo.data.net.base.BaseInterceptor
import io.meen.apollo.domain.utils.locale
import io.meen.common.net.HeaderUtils
import okhttp3.Request
import javax.inject.Inject

class LanguageHeaderInterceptor @Inject constructor(
    private val applicationContext: Context,
) : BaseInterceptor() {

    override fun processRequest(originalRequest: Request): Request {
        val language = applicationContext.locale().language
        return originalRequest.newBuilder()
            .addHeader(
                HeaderUtils.CLIENT_LANGUAGE,
                language.ifEmpty { HeaderUtils.DEFAULT_LANGUAGE_VALUE }
            )
            .build()
    }
}