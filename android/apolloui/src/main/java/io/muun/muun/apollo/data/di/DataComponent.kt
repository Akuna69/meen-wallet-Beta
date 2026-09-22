package io.meen.apollo.data.di

import android.content.Context
import app_provided_data.Config
import dagger.Component
import io.meen.apollo.data.afs.MetricsProvider
import io.meen.apollo.data.apis.DriveAuthenticator
import io.meen.apollo.data.apis.DriveUploader
import io.meen.apollo.data.async.gcm.GcmMessageListenerService
import io.meen.apollo.data.async.tasks.MeenWorkerFactory
import io.meen.apollo.data.async.tasks.TaskScheduler
import io.meen.apollo.data.db.DaoManager
import io.meen.apollo.data.db.contact.ContactDao
import io.meen.apollo.data.db.operation.OperationDao
import io.meen.apollo.data.db.public_profile.PublicProfileDao
import io.meen.apollo.data.external.HoustonConfig
import io.meen.apollo.data.external.NotificationService
import io.meen.apollo.data.net.HoustonClient
import io.meen.apollo.data.net.NetworkInfoProvider
import io.meen.apollo.data.nfc.NfcBridgerFactory
import io.meen.apollo.data.nfc.NfcEmpiricalCache
import io.meen.apollo.data.os.ClipboardProvider
import io.meen.apollo.data.os.Configuration
import io.meen.apollo.data.os.execution.ExecutionTransformerFactory
import io.meen.apollo.data.os.secure_storage.SecureStorageProvider
import io.meen.apollo.data.preferences.AuthRepository
import io.meen.apollo.data.preferences.ExchangeRateWindowRepository
import io.meen.apollo.data.preferences.FeeWindowRepository
import io.meen.apollo.data.preferences.FirebaseInstallationIdRepository
import io.meen.apollo.data.preferences.KeysRepository
import io.meen.apollo.data.preferences.RepositoryRegistry
import io.meen.apollo.data.preferences.UserRepository
import io.meen.apollo.domain.ApplicationLockManager
import io.meen.apollo.domain.SignupDraftManager
import io.meen.apollo.domain.action.LogoutActions
import io.meen.apollo.domain.action.di.ActionComponent
import io.meen.apollo.domain.analytics.Analytics
import io.meen.apollo.domain.libwallet.FeeBumpFunctionsProvider
import io.meen.apollo.domain.libwallet.LibwalletClient
import org.bitcoinj.core.NetworkParameters
import java.util.concurrent.Executor
import javax.inject.Singleton

/**
 * Dagger Component. {@link Component}.
 * Add here:
 * - members-injection methods (e.g for classes which lifecycles are 3rd-party controlled, like
 * Android's). Example: void inject(GcmMessageListenerService service).
 * - provision methods, to expose injected or provided dependencies to other (dependent) components.
 * Example: ClipboardProvider clipboardProvider();
 */
@Singleton
@Component(modules = [DataModule::class])
interface DataComponent : ActionComponent {

    fun inject(service: GcmMessageListenerService)

    fun inject(workerFactory: MeenWorkerFactory)

    // Exposed to dependent components

    fun backgroundExecutor(): Executor

    fun transformers(): ExecutionTransformerFactory

    fun houstonClient(): HoustonClient

    fun authRepository(): AuthRepository

    fun keysRepository(): KeysRepository

    fun userRepository(): UserRepository

    fun fcmTokenRepository(): FirebaseInstallationIdRepository

    fun contactDao(): ContactDao

    fun operationDao(): OperationDao

    fun publicProfileDao(): PublicProfileDao

    fun networkParameters(): NetworkParameters

    fun taskScheduler(): TaskScheduler

    fun exchangeRateWindowRepository(): ExchangeRateWindowRepository

    fun clipboardProvider(): ClipboardProvider

    fun networkInfoProvider(): NetworkInfoProvider

    fun context(): Context

    fun expectedFeeRepository(): FeeWindowRepository

    fun configuration(): Configuration

    fun secureStorageProvider(): SecureStorageProvider

    fun logoutActions(): LogoutActions

    fun applicationLockManager(): ApplicationLockManager

    fun signupDraftManager(): SignupDraftManager

    fun houstonConfig(): HoustonConfig

    fun driveAuthenticator(): DriveAuthenticator

    fun driveUploader(): DriveUploader

    fun repositoryRegistry(): RepositoryRegistry

    fun notificationService(): NotificationService

    fun analytics(): Analytics

    fun daoManager(): DaoManager

    fun libwalletConfig(): Config

    fun libwalletClient(): LibwalletClient

    fun nfcBridgerFactory(): NfcBridgerFactory

    fun nfcEmpiricalCache(): NfcEmpiricalCache

    fun feeBumpFunctionsProvider(): FeeBumpFunctionsProvider

    fun metricsProvider(): MetricsProvider
}
