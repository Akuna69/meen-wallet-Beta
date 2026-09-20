package io.meen.apollo.domain.selector

import io.meen.apollo.data.preferences.UserRepository
import io.meen.apollo.domain.model.BitcoinUnit
import rx.Observable
import javax.inject.Inject


class BitcoinUnitSelector @Inject constructor(private val userRepository: UserRepository) {

    fun watch(): Observable<BitcoinUnit> =
        userRepository.watchBitcoinUnit()

    fun get(): BitcoinUnit =
        watch().toBlocking().first()

}