package io.meen.apollo.presentation.ui.view;

import io.meen.apollo.presentation.model.text_decoration.DecorationHandler;

import android.content.Context;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatEditText;

public class MeenEditText extends AppCompatEditText implements DecorationHandler {

    public MeenEditText(Context context) {
        super(context);
    }

    public MeenEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MeenEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
