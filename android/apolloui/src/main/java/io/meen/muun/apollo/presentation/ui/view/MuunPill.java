package io.meen.apollo.presentation.ui.view;

import io.meen.apollo.R;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import butterknife.BindView;

import javax.annotation.Nullable;

public class MeenPill extends MeenView {

    static final ViewProps<MeenPill> viewProps = new ViewProps.Builder<MeenPill>()
            .addStringJava(android.R.attr.text, MeenPill::setText)
            .build();

    @BindView(R.id.meen_pill_picture)
    ProfilePictureView picture;

    @BindView(R.id.meen_pill_text)
    TextView text;

    public MeenPill(Context context) {
        super(context);
    }

    public MeenPill(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MeenPill(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.meen_pill;
    }

    @Override
    protected void setUp(Context context, @Nullable AttributeSet attrs) {
        super.setUp(context, attrs);

        if (attrs != null) {
            viewProps.transfer(attrs, this);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        post(this::adjustPictureSize);
    }

    public void setText(CharSequence content) {
        text.setText(content);
    }

    public void setPictureUri(@Nullable String pictureUri) {
        picture.setPictureUri(pictureUri);
    }

    public void setPictureUri(@Nullable Uri pictureUri) {
        picture.setPictureUri(pictureUri);
    }

    public void setPictureVisible(boolean isVisible) {
        picture.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    private void adjustPictureSize() {
        final int size = getMeasuredHeight();

        picture.getLayoutParams().width = size;
        picture.getLayoutParams().height = size;
    }
}
