package io.meen.apollo.presentation.ui.home;

import io.meen.apollo.presentation.ui.base.BaseView;

public interface HomeView extends BaseView {

    /**
     * Takes user to SecurityCenter screen.
     */
    void navigateToSecurityCenter();

    /**
     * Takes user to SecurityCenter screen.
     */
    void showWelcomeToMeenDialog();

    /**
     * Show Taproot celebration.
     */
    void showTaprootCelebration();
}
