package io.meen.apollo.utils.screens

import android.content.Context
import androidx.test.uiautomator.UiDevice
import io.meen.apollo.R
import io.meen.apollo.utils.WithMuunInstrumentationHelpers
import io.meen.apollo.utils.screens.RecommendedFeeScreen.OnScreenFeeOption
import java.text.DecimalFormat

class ManualFeeScreen(
    override val device: UiDevice,
    override val context: Context,
) : WithMuunInstrumentationHelpers {

    fun editFeeRate(feeRate: Double): OnScreenFeeOption {
        feeInput.text = DecimalFormat.getInstance(locale).format(feeRate);
        return feeOption
    }

    fun confirmFeeRate() {
        pressMuunButton(R.id.confirm_fee)
    }

    private val feeOption
        get() =
            OnScreenFeeOption(
                feeInput.text.parseDecimal(),
                id(R.id.fee_main_value).text.toMoney(),
                id(R.id.fee_secondary_value).text.dropParenthesis().toMoney()
            )

    private val feeInput
        get() =
            id(R.id.fee_manual_input).child(R.id.fee_input)
}