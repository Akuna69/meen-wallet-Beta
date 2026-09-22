package io.meen.apollo.domain.libwallet.model

import io.meen.common.crypto.hd.MeenOutput
import io.meen.common.utils.Encodings

class Outpoint(val output: MeenOutput) : libwallet.Outpoint {

    override fun amount(): Long =
        output.amount

    override fun index(): Long =
        output.index.toLong()

    override fun txId(): ByteArray =
        Encodings.hexToBytes(output.txId)
}