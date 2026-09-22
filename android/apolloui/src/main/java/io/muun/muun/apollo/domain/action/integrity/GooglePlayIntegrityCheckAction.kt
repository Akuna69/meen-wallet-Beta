package io.meen.apollo.domain.action.integrity

import io.meen.apollo.data.net.HoustonClient
import io.meen.apollo.data.net.base.NetworkException
import io.meen.apollo.data.os.execution.ExecutionTransformerFactory
import io.meen.apollo.data.preferences.PlayIntegrityNonceRepository
import io.meen.apollo.domain.errors.PlayIntegrityError
import io.meen.apollo.domain.errors.UNKNOWN_ERROR
import io.meen.apollo.domain.model.PlayIntegrityToken
import io.meen.common.rx.ObservableFn
import rx.Observable
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import javax.inject.Inject

class GooglePlayIntegrityCheckAction @Inject constructor(
    private val googlePlayIntegrity: GooglePlayIntegrity,
    private val playIntegrityNonceRepo: PlayIntegrityNonceRepository,
    private val transformerFactory: ExecutionTransformerFactory,
    private val houstonClient: HoustonClient,
) {

    fun run(): Observable<out PlayIntegrityToken?> {
        return Observable.defer {
            // If we have a nonce from Houston, we make the Google Play Integrity API call
            val nonce = playIntegrityNonceRepo.get()
            return@defer if (nonce != null) {
                googlePlayIntegrity.fetchIntegrityToken(nonce)
                    .observeOn(transformerFactory.backgroundScheduler)
                    .compose(buildRetryTransformer())
                    .onErrorResumeNext { error ->
                        if (error is PlayIntegrityError) {
                            Observable.just(
                                PlayIntegrityToken(null, error.getName(), error.getCause())
                            )
                        } else {
                            Observable.just(PlayIntegrityToken(null, UNKNOWN_ERROR, error.message))
                        }
                    }
                    .flatMapCompletable((houstonClient::submitPlayIntegrityToken))

            } else {
                Observable.just(null)
            }
        }
    }

    private fun buildRetryTransformer(): Observable.Transformer<PlayIntegrityToken, PlayIntegrityToken> {

        val retryPolicy = GooglePlayIntegrityRetryPolicy(5, 4, GooglePlayIntegrity.retryableErrors)

        return Observable.Transformer { observable ->
            observable.retryWhen(retryPolicy)
                .timeout(40, TimeUnit.SECONDS) // at least 3 attempts (5s, 10s, 20s)
                .compose(ObservableFn.replaceTypedError(TimeoutException::class.java) {
                    retryPolicy.lastError() ?: NetworkException(it)
                })
        }
    }
}