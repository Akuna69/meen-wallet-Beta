package io.meen.apollo.data.di

import android.content.Context
import app_provided_data.Config
import com.fasterxml.jackson.databind.ObjectMapper
import dagger.Module
import dagger.Provides
import io.grpc.ManagedChannel
import io.meen.apollo.data.afs.ActivityManagerInfoProvider
import io.meen.apollo.data.afs.AppInfoProvider
import io.meen.apollo.data.afs.BatteryInfoProvider
import io.meen.apollo.data.afs.BuildInfoProvider
import io.meen.apollo.data.afs.ConnectivityInfoProvider
import io.meen.apollo.data.afs.DateTimeZoneProvider
import io.meen.apollo.data.afs.FileInfoProvider
import io.meen.apollo.data.afs.HardwareCapabilitiesProvider
import io.meen.apollo.data.afs.LocaleInfoProvider
import io.meen.apollo.data.afs.MetricsProvider
import io.meen.apollo.data.afs.NfcExtendedApduProbe
import io.meen.apollo.data.afs.NfcProvider
import io.meen.apollo.data.afs.PackageManagerInfoProvider
import io.meen.apollo.data.afs.RuntimeInfoProvider
import io.meen.apollo.data.afs.SystemCapabilitiesProvider
import io.meen.apollo.data.afs.SystemInfoProvider
import io.meen.apollo.data.afs.TelephonyInfoProvider
import io.meen.apollo.data.afs.TrafficStatsInfoProvider
import io.meen.apollo.data.apis.DriveAuthenticator
import io.meen.apollo.data.apis.DriveImpl
import io.meen.apollo.data.apis.DriveUploader
import io.meen.apollo.data.db.DaoManager
import io.meen.apollo.data.db.contact.ContactDao
import io.meen.apollo.data.db.incoming_swap.IncomingSwapDao
import io.meen.apollo.data.db.incoming_swap.IncomingSwapHtlcDao
import io.meen.apollo.data.db.operation.OperationDao
import io.meen.apollo.data.db.phone_contact.PhoneContactDao
import io.meen.apollo.data.db.public_profile.PublicProfileDao
import io.meen.apollo.data.db.submarine_swap.SubmarineSwapDao
import io.meen.apollo.data.external.AppStandbyBucketProvider
import io.meen.apollo.data.external.Globals
import io.meen.apollo.data.external.HoustonConfig
import io.meen.apollo.data.external.NotificationService
import io.meen.apollo.data.fs.LibwalletDataDirectory
import io.meen.apollo.data.libwallet.HttpClientSessionProvider
import io.meen.apollo.data.libwallet.KeyProvider
import io.meen.apollo.data.libwallet.LibwalletSecureKeyValueStorage
import io.meen.apollo.data.libwallet.grpc.GrpcChannelFactory
import io.meen.apollo.data.net.NetworkInfoProvider
import io.meen.apollo.data.nfc.AndroidNfcBridge
import io.meen.apollo.data.nfc.NfcEmpiricalCache
import io.meen.apollo.data.os.Configuration
import io.meen.apollo.data.os.execution.ExecutionTransformerFactory
import io.meen.apollo.data.os.execution.JobExecutor
import io.meen.apollo.data.preferences.BackgroundTimesRepository
import io.meen.apollo.data.preferences.FeaturesRepository
import io.meen.apollo.data.preferences.RepositoryRegistry
import io.meen.apollo.domain.action.NotificationActions
import io.meen.apollo.domain.action.NotificationPoller
import io.meen.apollo.domain.action.session.IsRootedDeviceAction
import io.meen.apollo.domain.analytics.Analytics
import io.meen.apollo.domain.libwallet.FeeBumpFunctionsProvider
import io.meen.apollo.domain.libwallet.LibwalletClient
import io.meen.apollo.domain.libwallet.LibwalletFeeBumpFunctionsProvider
import io.meen.apollo.domain.libwallet.LibwalletLogAdapter
import io.meen.apollo.domain.selector.BitcoinUnitSelector
import io.meen.apollo.presentation.app.AppStandbyBucketProviderImpl
import io.meen.apollo.presentation.app.NotificationServiceImpl
import io.reactivex.schedulers.Schedulers
import org.bitcoinj.core.NetworkParameters
import rx.Scheduler
import rx.android.schedulers.AndroidSchedulers
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import javax.inject.Named
import javax.inject.Singleton

@Module
class DataModule(
    private val applicationContext: Context,
    private val houstonConfig: HoustonConfig,
) {

    @Provides
    @Singleton
    fun provideContext(): Context = applicationContext

    @Provides
    @Singleton
    fun provideDataModule(): DataModule = this

    /**
     * Provide a dao manager with logging enabled.
     */
    @Provides
    @Singleton
    fun provideDaoManager(
        context: Context,
        contactDao: ContactDao,
        operationDao: OperationDao,
        phoneContactDao: PhoneContactDao,
        publicProfileDao: PublicProfileDao,
        submarineSwapDao: SubmarineSwapDao,
        incomingSwapDao: IncomingSwapDao,
        incomingSwapHtlcDao: IncomingSwapHtlcDao,
        config: Configuration,
        executor: Executor,
    ): DaoManager = DaoManager(
        context,
        config.getString("database.filename"),
        config.getInt("database.version"),
        Schedulers.from(executor),
        contactDao,
        operationDao,
        phoneContactDao,
        publicProfileDao,
        submarineSwapDao,
        incomingSwapHtlcDao,
        incomingSwapDao
    )

    @Provides
    @Singleton
    fun provideNotificationService(
        context: Context,
        executionTransformerFactory: ExecutionTransformerFactory,
        bitcoinUnitSelector: BitcoinUnitSelector,
        analytics: Analytics,
    ): NotificationService = NotificationServiceImpl(
        context,
        executionTransformerFactory,
        bitcoinUnitSelector,
        analytics
    )

    @Provides
    @Singleton
    fun provideNetworkParameters(): NetworkParameters = houstonConfig.getNetwork()

    @Provides
    @Singleton
    fun provideExecutor(jobExecutor: JobExecutor): Executor = jobExecutor

    @Provides
    @Singleton
    @Named("mainThreadScheduler")
    fun provideScheduler(): Scheduler = AndroidSchedulers.mainThread()

    @Provides
    @Singleton
    @Named("notificationScheduler")
    fun provideNotificationScheduler(
    ): Scheduler = rx.schedulers.Schedulers.from(Executors.newSingleThreadScheduledExecutor { r ->
        Thread(r, "notifications")
    })

    @Provides
    @Singleton
    fun provideObjectMapper(): ObjectMapper = ObjectMapper()

    @Provides
    @Singleton
    fun provideHoustonConfig(): HoustonConfig = houstonConfig

    @Provides
    @Singleton
    fun provideGoogleDriveAuthenticator(
        singletonImpl: DriveImpl,
    ): DriveAuthenticator = singletonImpl

    @Provides
    @Singleton
    fun provideGoogleDriveUploader(singletonImpl: DriveImpl): DriveUploader = singletonImpl

    @Provides
    @Singleton
    fun provideAppStandbyBucketProvider(
        context: Context,
    ): AppStandbyBucketProvider = AppStandbyBucketProviderImpl(context)

    @Provides
    @Singleton
    fun provideRepositoryRegistry(): RepositoryRegistry = RepositoryRegistry()

    // TODO: this should be extracted to a DomainModule or similar
    @Provides
    @Singleton
    fun provideNotificationPoller(
        notificationActions: NotificationActions,
    ): NotificationPoller = notificationActions

    @Provides
    @Singleton
    fun provideLibwalletConfig(
        libwalletDataDirectory: LibwalletDataDirectory,
        featuresRepository: FeaturesRepository,
        httpClientSessionProvider: HttpClientSessionProvider,
        nfcBridge: AndroidNfcBridge,
        keyProvider: KeyProvider,
        secureKeyValueStorage: LibwalletSecureKeyValueStorage,
    ): Config {
        libwalletDataDirectory.ensureExists()
        val dataDir = libwalletDataDirectory.path.absolutePath
        val socketName = libwalletDataDirectory.socket.absolutePath

        val config = Config()
        config.dataDir = dataDir
        config.socketPath = socketName
        config.featureStatusProvider = featuresRepository
        config.appLogSink = LibwalletLogAdapter()
        config.httpClientSessionProvider = httpClientSessionProvider
        config.nfcBridge = nfcBridge
        config.keyProvider = keyProvider
        config.secureKeyValueStorage = secureKeyValueStorage

        val network = when (Globals.INSTANCE.network.getId()) {
            NetworkParameters.ID_MAINNET -> "mainnet"
            NetworkParameters.ID_REGTEST -> "regtest"
            NetworkParameters.ID_TESTNET -> "testnet3"
            else -> throw IllegalArgumentException(
                "Unknown network id " + Globals.INSTANCE.network.getId()
            )
        }

        config.network = network

        return config
    }

    @Provides
    @Singleton
    fun provideGrpcChannel(
        libwalletConfig: Config,
    ): ManagedChannel = GrpcChannelFactory.create(libwalletConfig.socketPath)

    @Provides
    @Singleton
    fun provideLibwalletClient(channel: ManagedChannel): LibwalletClient = LibwalletClient(channel)

    @Provides
    @Singleton
    fun provideNfcBridge(): AndroidNfcBridge = AndroidNfcBridge()

    @Provides
    fun provideFeeBumpFunctionsProvider(): FeeBumpFunctionsProvider =
        LibwalletFeeBumpFunctionsProvider()

    @Provides
    @Singleton
    fun provideMetricsProvider(
        context: Context,
        nfcEmpiricalCache: NfcEmpiricalCache,
    ): MetricsProvider {
        val activityManagerInfoProvider = ActivityManagerInfoProvider(context)
        val telephonyInfoProvider = TelephonyInfoProvider(context)
        val hardwareCapabilitiesProvider = HardwareCapabilitiesProvider(context)
        val packageManagerInfoProvider = PackageManagerInfoProvider(context)
        val buildInfoProvider = BuildInfoProvider()
        val fileInfoProvider = FileInfoProvider(context)
        val systemCapabilitiesProvider = SystemCapabilitiesProvider(context)
        val backgroundTimesRepository =
            BackgroundTimesRepository(context, provideRepositoryRegistry())
        val appInfoProvider = AppInfoProvider(context, backgroundTimesRepository)
        val connectivityInfoProvider = ConnectivityInfoProvider(context)
        val dateTimeZoneProvider = DateTimeZoneProvider(context)
        val localeInfoProvider = LocaleInfoProvider()
        val trafficStatsInfoProvider = TrafficStatsInfoProvider()
        val nfcExtendedApduProbe = NfcExtendedApduProbe(context)
        val nfcProvider = NfcProvider(context, nfcExtendedApduProbe, nfcEmpiricalCache)
        val batteryInfoProvider = BatteryInfoProvider(context)
        val systemInfoProvider = SystemInfoProvider()
        val networkInfoProvider = NetworkInfoProvider(context)
        val isRootedDeviceAction = IsRootedDeviceAction(context)
        val runtimeInfoProvider = RuntimeInfoProvider(context)

        return MetricsProvider(
            activityManagerInfoProvider,
            telephonyInfoProvider,
            hardwareCapabilitiesProvider,
            packageManagerInfoProvider,
            buildInfoProvider,
            fileInfoProvider,
            systemCapabilitiesProvider,
            appInfoProvider,
            connectivityInfoProvider,
            dateTimeZoneProvider,
            localeInfoProvider,
            trafficStatsInfoProvider,
            nfcProvider,
            batteryInfoProvider,
            systemInfoProvider,
            networkInfoProvider,
            isRootedDeviceAction,
            runtimeInfoProvider
        )
    }
}