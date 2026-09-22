package io.meen.apollo.domain.model

import io.meen.common.crypto.hd.MeenAddress

class MeenAddressGroup(
    val legacy: MeenAddress,
    val segwit: MeenAddress,
    val taproot: MeenAddress,
) {

    fun toAddressGroup() =
        AddressGroup(legacy.address, segwit.address, taproot.address)
}

class AddressGroup(
    val legacy: String,
    val segwit: String,
    val taproot: String,
) {

    fun getAddress(addressType: AddressType) =
        when (addressType) {
            AddressType.LEGACY -> legacy
            AddressType.SEGWIT -> segwit
            AddressType.TAPROOT -> taproot
        }
}