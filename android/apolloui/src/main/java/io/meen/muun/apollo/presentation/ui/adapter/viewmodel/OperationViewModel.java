package io.meen.apollo.presentation.ui.adapter.viewmodel;

import io.meen.apollo.presentation.model.UiOperation;
import io.meen.apollo.presentation.ui.adapter.holder.ViewHolderFactory;

public class OperationViewModel implements ItemViewModel {

    public final UiOperation operation;

    /**
     * Constructor.
     */
    public OperationViewModel(UiOperation operation) {
        this.operation = operation;
    }

    @Override
    public int type(ViewHolderFactory typeFactory) {
        return typeFactory.getLayoutRes(operation);
    }
}
