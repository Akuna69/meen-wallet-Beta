package io.meen.apollo.presentation.ui.view;

import android.view.View;
import androidx.recyclerview.widget.RecyclerView;

public class MeenViewHolder<T extends View> extends RecyclerView.ViewHolder {

    private final T view;

    public MeenViewHolder(T view) {
        super(view);
        this.view = view;
    }

    public T getView() {
        return view;
    }
}
