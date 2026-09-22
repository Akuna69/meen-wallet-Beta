package io.meen.apollo.data.preferences

import android.content.Context
import app_provided_data.BackendActivatedFeatureStatusProvider
import io.meen.apollo.data.preferences.adapter.JsonPreferenceAdapter
import io.meen.apollo.data.preferences.rx.Preference
import io.meen.apollo.data.preferences.stored.StoredBackendFeatures
import io.meen.apollo.domain.model.MeenFeature
import rx.Observable
import timber.log.Timber
import javax.inject.Inject

// Open for mockito to mock/spy
open class FeaturesRepository @Inject constructor(
    context: Context,
    repositoryRegistry: RepositoryRegistry,
) : BaseRepository(context, repositoryRegistry), BackendActivatedFeatureStatusProvider {

    companion object {
        private const val BACKEND_FEATURES = "backend_features"
    }

    private val backendFeaturesPref: Preference<StoredBackendFeatures>
        get() = rxSharedPreferences
            .getObject(
                BACKEND_FEATURES,
                StoredBackendFeatures(),
                JsonPreferenceAdapter(StoredBackendFeatures::class.java)
            )
    override val fileName get() = "features"

    /**
     * Fetch an observable instance of the currently supported backend features.
     */
    fun fetch(): Observable<List<MeenFeature>> {
        return backendFeaturesPref.asObservable()
            .map { it.features.map(MeenFeature.Companion::fromLibwalletModel) }
    }

    /**
     * Store a new set of backend features.
     */
    open fun store(newFeatures: List<MeenFeature>) {
        val storedBackendFeatures = backendFeaturesPref.get()!! // Has default value
        storedBackendFeatures.features = newFeatures.map { it.toLibwalletModel() }
        backendFeaturesPref.set(storedBackendFeatures)
    }

    /**
     * Check for a specific feature flag, passed as a string.
     * Not to be used by the app. This is a bridge to provide feature flag information to libwallet
     * until we implement a more generic libwallet-side storage mechanism.
     */
    override fun isBackendFlagEnabled(flag: String?): Boolean {
        if (flag == null) {
            Timber.e("Tried to read null feature flag from libwallet.")
            return false
        }

        return backendFeaturesPref.get()?.features?.contains(flag) == true
    }
}
