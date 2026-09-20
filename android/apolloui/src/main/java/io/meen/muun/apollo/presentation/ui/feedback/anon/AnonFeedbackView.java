package io.meen.apollo.presentation.ui.feedback.anon;

import io.meen.apollo.presentation.ui.base.BaseView;
import io.meen.common.Optional;

public interface AnonFeedbackView extends BaseView {

    String SUPPORT_ID = "support_id";

    void setSupportId(Optional<String> maybeSupportId);

}
