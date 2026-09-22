package io.meen.apollo.presentation.ui.feedback.email;

import io.meen.apollo.presentation.ui.base.BaseView;

public interface FeedbackView extends BaseView {

    String FEEDBACK_CATEGORY = "context";
    String FEEDBACK_EXPLANATION = "explanation";

    void setExplanation(String text);

    void setLoading(boolean isLoading);

    void onSubmitSuccess();

}
