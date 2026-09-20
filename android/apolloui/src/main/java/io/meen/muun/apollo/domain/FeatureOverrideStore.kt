package io.meen.apollo.domain

import io.meen.apollo.domain.analytics.Analytics
import io.meen.apollo.domain.analytics.AnalyticsEvent
import io.meen.apollo.domain.libwallet.LibwalletClient
import io.meen.apollo.domain.model.MeenFeature
import timber.log.Timber
import javax.inject.Inject

/**
 * Abstracts dogfood functionality that allows users to manually disable some feature flags.
 * If a MeenFeature is overridden, it is effectively disabled.
 * This works as a selector but also allows writes.
 */
class FeatureOverrideStore @Inject constructor(
    private val libwalletClient: LibwalletClient,
    private val analytics: Analytics,
) {

    companion object {
        private const val FEATURE_FLAG_OVERRIDE_PREFIX = "featureFlagOverrides:"
    }

    fun getFeatureOverrides(): List<MeenFeature.OverridableFeature.Overridable> {

        // Check all features for overrides
        val overrides = MeenFeature.entries
            .filter { feature -> feature.isOverridable() }
            .map { meenFeature ->
                meenFeature.toOverridableFeature() as MeenFeature.OverridableFeature.Overridable
            }
            .filter { isOverridden(it) }

        Timber.d("Overridden Feature Flags: ${overrides.joinToString { it.feature.name }}")

        return overrides
    }

    private fun isOverridden(feature: MeenFeature.OverridableFeature.Overridable): Boolean {
        val key = getLibwalletStorageKey(feature)
        return libwalletClient.getBoolean(key, false)
    }

    private fun storeOverride(
        overridableFeature: MeenFeature.OverridableFeature.Overridable,
        isOverridden: Boolean,
    ) {

        val key = getLibwalletStorageKey(overridableFeature)
        libwalletClient.saveBoolean(key, isOverridden)

        val feature = overridableFeature.feature
        analytics.report(AnalyticsEvent.E_FEATURE_FLAG_OVERRIDE(feature.name, isOverridden))
    }

    /**
     * Convenience method. Should be used sparsely and only if you know what you're doing.
     */
    fun disableFeatureFlag(meenFeature: MeenFeature) {
        if (meenFeature.isOverridable()) {
            val overridableFeature = meenFeature.toOverridableFeature()
                as MeenFeature.OverridableFeature.Overridable
            disableFeatureFlag(overridableFeature)

        } else {
            throw IllegalStateException("Not overridable Feature: $meenFeature")
        }
    }

    fun disableFeatureFlag(overridableFeature: MeenFeature.OverridableFeature.Overridable) {
        storeOverride(overridableFeature, true)
    }

    fun enableFeatureFlag(overridableFeature: MeenFeature.OverridableFeature.Overridable) {
        storeOverride(overridableFeature, false)
    }

    private fun getLibwalletStorageKey(
        overridableFeature: MeenFeature.OverridableFeature.Overridable,
    ): String {
        return FEATURE_FLAG_OVERRIDE_PREFIX + overridableFeature.libwalletKeySuffix
    }
}