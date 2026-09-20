package io.meen.apollo.presentation.ui.show_qr

import androidx.annotation.StringRes
import io.meen.apollo.R

enum class ShowQrPage(@StringRes val titleRes: Int) {
    BITCOIN(R.string.tab_bitcoin_address),
    LN(R.string.tab_ln_invoice);
}