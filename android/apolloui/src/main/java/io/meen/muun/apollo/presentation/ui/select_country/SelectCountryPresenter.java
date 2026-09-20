package io.meen.apollo.presentation.ui.select_country;

import io.meen.apollo.presentation.ui.base.BasePresenter;
import io.meen.apollo.presentation.ui.base.BaseView;
import io.meen.apollo.presentation.ui.base.di.PerActivity;

import javax.inject.Inject;

@PerActivity
public class SelectCountryPresenter extends BasePresenter<BaseView> {

    @Inject
    public SelectCountryPresenter() {
        super();
    }
}
