package io.meen.apollo.domain.libwallet.model

import io.meen.common.crypto.hd.MeenAddress

class Address(val address: MeenAddress) : libwallet.MeenAddress {

    override fun derivationPath(): String =
        address.derivationPath

    override fun version(): Long =
        address.version.toLong()

    override fun address(): String =
        address.address
}