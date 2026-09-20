package io.meen.apollo.presentation.ui.settings;

import io.meen.apollo.presentation.ui.base.SingleFragmentView;

interface EmailWaitView extends SingleFragmentView {

    void handleInvalidLinkError();

    void handleExpiredLinkError();
}
