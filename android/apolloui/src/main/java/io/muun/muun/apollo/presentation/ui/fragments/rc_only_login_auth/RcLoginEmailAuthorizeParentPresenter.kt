package io.meen.apollo.presentation.ui.fragments.rc_only_login_auth

import io.meen.apollo.presentation.ui.base.ParentPresenter

interface RcLoginEmailAuthorizeParentPresenter : ParentPresenter {

    fun getObfuscatedEmail(): String

    fun cancelRcLoginEmailAuth()

    fun reportRcLoginEmailVerified()
}
