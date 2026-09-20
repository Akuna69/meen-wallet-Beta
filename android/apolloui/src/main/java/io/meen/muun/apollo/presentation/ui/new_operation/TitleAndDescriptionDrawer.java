package io.meen.apollo.presentation.ui.new_operation;

import io.meen.apollo.R;
import io.meen.apollo.presentation.ui.view.DrawerDialogFragment;
import io.meen.apollo.presentation.ui.view.MeenActionDrawer;

import android.content.Context;
import android.text.method.LinkMovementMethod;
import android.util.AttributeSet;
import android.widget.TextView;
import androidx.annotation.NonNull;
import butterknife.BindView;

import javax.annotation.Nullable;

public class TitleAndDescriptionDrawer extends DrawerDialogFragment {

    CharSequence description;

    public TitleAndDescriptionDrawer() {
    }

    public void setDescription(CharSequence description) {
        this.description = description;
    }
    
    @NonNull
    protected MeenActionDrawer createActionDrawer() {
        final MeenDescriptionDrawer meenActionDrawer = new MeenDescriptionDrawer(requireContext());
        meenActionDrawer.setDescription(description);
        return meenActionDrawer;
    }

    public static class MeenDescriptionDrawer extends MeenActionDrawer {

        @BindView(R.id.meen_action_drawer_description)
        TextView descriptionTextView;

        public MeenDescriptionDrawer(Context context) {
            super(context);
        }

        public MeenDescriptionDrawer(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        protected void setUp(Context context, @Nullable AttributeSet attrs) {
            super.setUp(context, attrs);

            descriptionTextView.setMovementMethod(LinkMovementMethod.getInstance());
        }

        public void setDescription(CharSequence description) {
            descriptionTextView.setText(description);
        }

        @Override
        protected int getLayoutResource() {
            return R.layout.drawer_title_and_description;
        }
    }
}
