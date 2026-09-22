package io.meen.apollo.presentation.ui.fragments.profile;

import io.meen.apollo.domain.action.base.ActionState;
import io.meen.apollo.domain.model.user.UserProfile;
import io.meen.apollo.presentation.ui.base.ParentPresenter;
import io.meen.common.Optional;

import android.net.Uri;
import rx.Observable;

public interface ProfileParentPresenter extends ParentPresenter {

    /**
     * Submit a public profile as part of the P2P setup.
     */
    void submitProfile(String firstName, String lastName, Optional<Uri> pictureUri);

    /**
     * Get an Observable for the SubmitProfile async action.
     */
    Observable<ActionState<UserProfile>> watchSubmitProfile();

}
