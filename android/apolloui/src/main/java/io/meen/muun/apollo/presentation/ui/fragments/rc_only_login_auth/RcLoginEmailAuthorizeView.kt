package io.meen.apollo.presentation.ui.fragments.rc_only_login_auth

import io.meen.apollo.presentation.ui.base.SingleFragmentView

interface RcLoginEmailAuthorizeView : SingleFragmentView {

    fun setObfuscatedEmail(obfuscatedEmail: String)

    fun handleInvalidLinkError()

    fun handleExpiredLinkError()
}