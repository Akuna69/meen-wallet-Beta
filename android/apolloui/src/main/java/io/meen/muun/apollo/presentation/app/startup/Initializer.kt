package io.meen.apollo.presentation.app.startup

import rx.Completable

interface Initializer {

    fun init(): Completable
    fun dependencies(): Set<Class<out Initializer>> = emptySet()
}
