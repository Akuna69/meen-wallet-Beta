package io.meen.apollo.presentation.app.di.modules

import dagger.Binds
import dagger.Module
import io.meen.apollo.presentation.biometrics.BiometricsController
import io.meen.apollo.presentation.biometrics.BiometricsControllerImpl

@Module
interface BiometricsModule {

    @Binds
    fun bindBiometricsController(impl: BiometricsControllerImpl): BiometricsController
}
