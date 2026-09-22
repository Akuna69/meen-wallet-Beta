package io.meen.apollo.presentation.ui.view;

import io.meen.apollo.R;

import android.content.Context;
import android.util.AttributeSet;

public class FirstOnMeenView extends MeenView {

    public FirstOnMeenView(Context context) {
        super(context);
    }

    public FirstOnMeenView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FirstOnMeenView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.first_on_meen_view;
    }
}
