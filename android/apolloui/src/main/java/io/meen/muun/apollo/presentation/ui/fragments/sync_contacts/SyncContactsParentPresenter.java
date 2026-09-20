package io.meen.apollo.presentation.ui.fragments.sync_contacts;

import io.meen.apollo.presentation.ui.base.ParentPresenter;

public interface SyncContactsParentPresenter extends ParentPresenter {

    void reportContactPermissionGranted();

    void reportContactsPermissionNeverAskAgain();
}
