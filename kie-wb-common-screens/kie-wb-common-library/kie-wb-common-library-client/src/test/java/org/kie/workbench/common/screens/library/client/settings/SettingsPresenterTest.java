package org.kie.workbench.common.screens.library.client.settings;

import org.junit.Before;
import org.junit.Test;

public class SettingsPresenterTest {

    @Before
    public void before() {
        final SettingsPresenter settingsPresenter =
                new SettingsPresenter(null,
                                      null,
                                      null,
                                      null,
                                      null,
                                      null,
                                      null,
                                      null,
                                      null,
                                      null);

        settingsPresenter.setup();
    }

    @Test
    public void test() {

    }
}