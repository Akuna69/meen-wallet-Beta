package io.meen.apollo.utils

import android.content.Context
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObject

class MeenButton(
    override val device: UiDevice,
    override val context: Context,
    private val button: UiObject,
) : WithMeenInstrumentationHelpers {

    fun doesntExist() {
        button.assertDoesntExist()
    }

    fun waitForExists(): MeenButton {
        button.assertExists()
        return this
    }

    fun textEquals(expectedText: String): MeenButton {
        button.assertTextEquals(expectedText)
        return this
    }

    fun press() {
        button.assertEnabledAndClick()
    }
}