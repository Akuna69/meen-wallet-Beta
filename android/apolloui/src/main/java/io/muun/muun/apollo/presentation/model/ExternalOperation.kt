package io.meen.apollo.presentation.model

import android.content.Context
import android.text.TextUtils
import io.meen.apollo.R
import io.meen.apollo.domain.model.BitcoinUnit
import io.meen.apollo.domain.model.Operation
import io.meen.apollo.domain.utils.isEmpty
import io.meen.apollo.presentation.ui.utils.LinkBuilder
import io.meen.apollo.presentation.ui.utils.Uri

class ExternalOperation(
    operation: Operation,
    linkBuilder: LinkBuilder,
    bitcoinUnit: BitcoinUnit,
    context: Context
) : UiOperation(operation, linkBuilder, bitcoinUnit, context) {

    override fun getFormattedTitle(context: Context, shortName: Boolean): CharSequence =
        if (isCyclical) {
            context.getString(R.string.operation_sent_to_yourself)
        } else if (isIncoming) {
            if (isIncomingSwap && !lnUrlSender.isEmpty()) {
                context.getString(R.string.lnurl_withdraw_op_detail_title, lnUrlSender)
            } else {
                context.getString(R.string.external_incoming_operation)
            }
        } else {
            if (isSwap && !TextUtils.isEmpty(swapReceiverAlias)) {
                context.getString(R.string.external_outgoing_swap_operation, swapReceiverAlias)
            } else {
                context.getString(R.string.external_outgoing_operation)
            }
        }

    override fun getPictureUri(context: Context): String {
        val resId: Int = if (isSwap || isIncomingSwap) {
            R.drawable.lightning
        } else {
            R.drawable.btc
        }

        return Uri.getResourceUri(context, resId).toString()
    }
}