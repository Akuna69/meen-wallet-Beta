package io.meen.apollo.domain

import io.meen.apollo.BaseTest
import io.meen.apollo.data.external.Gen
import io.meen.common.bitcoinj.ValidationHelpers
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class GenTest : BaseTest() {

    @Test
    fun `random generated email is valid`() {

        for (i in 1 until 10) {
            assertThat(ValidationHelpers.isValidEmail(Gen.email())).isTrue()
        }
    }
}