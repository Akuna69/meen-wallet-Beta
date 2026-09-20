package io.meen.apollo.domain

import io.meen.apollo.data.external.Globals
import io.meen.apollo.data.external.UserFacingErrorMessages
import io.meen.apollo.data.logging.LoggingContext
import org.junit.BeforeClass
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner


@RunWith(MockitoJUnitRunner::class)
abstract class BaseUnitTest {

    companion object {

        @BeforeClass
        @JvmStatic
        fun classSetUp() {
            LoggingContext.sendToCrashlytics = false
            Globals.INSTANCE = Mockito.mock(Globals::class.java)
            UserFacingErrorMessages.INSTANCE =
                Mockito.mock(UserFacingErrorMessages::class.java) { "Foo" }
        }
    }
}