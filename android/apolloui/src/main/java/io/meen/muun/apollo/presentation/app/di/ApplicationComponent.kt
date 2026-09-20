package io.meen.apollo.presentation.app.di

import dagger.Component
import io.meen.apollo.data.di.DataComponent
import io.meen.apollo.presentation.app.ApolloApplication
import io.meen.apollo.presentation.app.di.modules.BiometricsModule
import io.meen.apollo.presentation.app.di.modules.InAppUpdateModule
import io.meen.apollo.presentation.app.di.modules.StartupModule
import io.meen.apollo.presentation.ui.base.di.ActivityComponent
import io.meen.apollo.presentation.ui.base.di.FragmentComponent
import io.meen.apollo.presentation.ui.base.di.ViewComponent

@PerApplication
@Component(
    dependencies = [DataComponent::class],
    modules = [StartupModule::class, BiometricsModule::class, InAppUpdateModule::class],
)
interface ApplicationComponent {

    fun inject(application: ApolloApplication)

    fun fragmentComponent(): FragmentComponent

    fun activityComponent(): ActivityComponent

    fun viewComponent(): ViewComponent
}
