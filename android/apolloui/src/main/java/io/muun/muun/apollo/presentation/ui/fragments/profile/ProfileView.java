package io.meen.apollo.presentation.ui.fragments.profile;

import io.meen.apollo.domain.errors.UserFacingError;
import io.meen.apollo.presentation.ui.base.SingleFragmentView;

public interface ProfileView extends SingleFragmentView {

    void setFirstNameError(UserFacingError error);

    void setLastNameError(UserFacingError error);

    void setLoading(boolean isLoading);
}
