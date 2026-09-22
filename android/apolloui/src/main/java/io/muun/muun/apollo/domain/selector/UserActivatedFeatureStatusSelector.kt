package io.meen.apollo.domain.selector

import io.meen.apollo.data.external.Globals
import io.meen.apollo.data.preferences.BlockchainHeightRepository
import io.meen.apollo.data.preferences.UserRepository
import io.meen.apollo.domain.libwallet.toLibwallet
import io.meen.apollo.domain.model.MeenFeature
import io.meen.apollo.domain.model.UserActivatedFeatureStatus
import io.meen.apollo.domain.model.user.User
import io.meen.apollo.domain.utils.toLibwalletIntList
import io.meen.apollo.domain.utils.toLibwalletModel
import libwallet.Libwallet
import libwallet.UserActivatedFeature
import rx.Observable
import javax.inject.Inject

class UserActivatedFeatureStatusSelector @Inject constructor(
    private val userRepository: UserRepository,
    private val blockchainHeightRepository: BlockchainHeightRepository,
    private val featureSelector: FeatureSelector,
) {

    companion object {
        val UAF_TAPROOT: UserActivatedFeature = Libwallet.getUserActivatedFeatureTaproot()
    }

    fun watchTaproot(): Observable<UserActivatedFeatureStatus> {
        return watch(UAF_TAPROOT)
    }

    fun watch(feature: UserActivatedFeature): Observable<UserActivatedFeatureStatus> {
        return Observable.combineLatest(
            userRepository.fetch(),
            blockchainHeightRepository.fetch(),
            featureSelector.fetch(),
            Observable.just(feature),
            this::combineState
        )
    }

    private fun combineState(
        user: User,
        blockHeight: Int,
        backendFeatures: List<MeenFeature>,
        wantedFeature: UserActivatedFeature,
    ): UserActivatedFeatureStatus {

        val uafStatus = Libwallet.determineUserActivatedFeatureStatus(
            wantedFeature,
            blockHeight.toLong(),
            user.emergencyKitVersions.toLibwalletIntList(),
            backendFeatures.map { it.toLibwalletModel() }.toLibwalletModel(),
            Globals.INSTANCE.network.toLibwallet()
        )

        return UserActivatedFeatureStatus.fromLibwalletModel(uafStatus)
    }

    fun get(feature: UserActivatedFeature): UserActivatedFeatureStatus =
        watch(feature).toBlocking().first()

}