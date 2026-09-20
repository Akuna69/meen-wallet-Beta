package io.meen.apollo.domain.model.user

import io.meen.apollo.domain.model.EmergencyKitExport
import io.meen.common.Supports
import io.meen.common.utils.Since
import org.threeten.bp.ZonedDateTime

class EmergencyKit(
    @Since(apolloVersion = 72)
    val lastExportedAt: ZonedDateTime,

    @Since(apolloVersion = Supports.Taproot.APOLLO)
    val version: Int,

    // For users who exported EK prior to this we can't know which export method was used
    @Since(apolloVersion = Supports.Taproot.APOLLO)
    val exportMethod: EmergencyKitExport.Method? = null
)