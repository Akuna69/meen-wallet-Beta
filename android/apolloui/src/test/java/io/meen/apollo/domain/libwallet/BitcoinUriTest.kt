package io.meen.apollo.domain.libwallet

import io.meen.apollo.BaseTest
import io.meen.apollo.domain.model.BitcoinAmount
import io.meen.common.utils.BitcoinUtils
import org.assertj.core.api.Assertions
import org.junit.Test


internal class BitcoinUriTest : BaseTest() {

    @Test
    fun testBitcoinUriToString() {

        val btcAmount = BitcoinAmount(
            19,
            BitcoinUtils.satoshisToBitcoins(19),
            BitcoinUtils.satoshisToBitcoins(19)
        )

        Assertions.assertThat(BitcoinUri.toString(btcAmount)).isEqualTo("0.00000019")
    }
}