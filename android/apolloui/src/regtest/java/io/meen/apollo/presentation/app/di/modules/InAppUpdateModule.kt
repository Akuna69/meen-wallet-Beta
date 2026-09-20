package io.meen.apollo.presentation.app.di.modules

import dagger.Module
import dagger.Provides
import io.meen.apollo.presentation.ui.home.InAppUpdateManager
import io.meen.apollo.presentation.ui.home.NoOpInAppUpdateManager

@Module
object InAppUpdateModule {

    @Provides
    fun provideInAppUpdateManagerFactory(): InAppUpdateManager.Factory =
        InAppUpdateManager.Factory { _, _ -> NoOpInAppUpdateManager() }
}
