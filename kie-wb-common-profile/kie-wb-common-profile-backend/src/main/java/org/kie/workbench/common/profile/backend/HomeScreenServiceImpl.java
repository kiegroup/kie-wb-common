package org.kie.workbench.common.profile.backend;

import javax.inject.Inject;

import org.kie.workbench.common.profile.api.preferences.HomeScreenService;
import org.kie.workbench.common.profile.api.preferences.Profile;
import org.kie.workbench.common.profile.api.preferences.ProfilePreferencesPortableGeneratedImpl;
import org.uberfire.preferences.shared.bean.PreferenceBeanServerStore;

public class HomeScreenServiceImpl implements HomeScreenService {

    @Inject
    public PreferenceBeanServerStore preferenceBeanServerStore;

    @Override
    public Profile profilePreference() {
        return preferenceBeanServerStore.load(new ProfilePreferencesPortableGeneratedImpl()).getProfile();
    }
}
