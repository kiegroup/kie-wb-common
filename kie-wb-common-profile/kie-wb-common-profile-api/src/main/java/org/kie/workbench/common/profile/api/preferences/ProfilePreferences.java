package org.kie.workbench.common.profile.api.preferences;

import org.uberfire.preferences.shared.PropertyFormType;
import org.uberfire.preferences.shared.annotations.Property;
import org.uberfire.preferences.shared.annotations.WorkbenchPreference;
import org.uberfire.preferences.shared.bean.BasePreference;

@WorkbenchPreference(identifier = "ProfilePreferences", bundleKey = "ProfilePreferences.Label")
public class ProfilePreferences implements BasePreference<ProfilePreferences> {

    @Property(bundleKey = "ProfilePreferences.Profiles", 
            helpBundleKey= "ProfilePreferences.Profiles.Help", 
            formType = PropertyFormType.COMBO)
    private Profile profile;

    

    public ProfilePreferences() {
    }
    
    public ProfilePreferences(Profile profile) {
        this.profile = profile;
    }

    @Override
    public ProfilePreferences defaultValue(ProfilePreferences defaultValue) {
        defaultValue.setProfile(Profile.FULL);
        return defaultValue;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }
   
}
