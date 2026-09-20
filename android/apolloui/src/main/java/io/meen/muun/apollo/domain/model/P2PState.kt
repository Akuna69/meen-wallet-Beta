package io.meen.apollo.domain.model

import io.meen.apollo.domain.action.base.ActionState
import io.meen.apollo.domain.model.user.User

class P2PState(
    val user: User,
    val permissionState: PermissionState,
    val syncState: ActionState<*>,
    val contacts: List<Contact>
)