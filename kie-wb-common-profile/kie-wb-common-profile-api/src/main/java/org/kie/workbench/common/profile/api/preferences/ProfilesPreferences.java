package org.kie.workbench.common.profile.api.preferences;

import org.uberfire.preferences.shared.PropertyFormType;
import org.uberfire.preferences.shared.annotations.Property;
import org.uberfire.preferences.shared.annotations.WorkbenchPreference;
import org.uberfire.preferences.shared.bean.BasePreference;

@WorkbenchPreference(identifier = "ProfilesPreferences", bundleKey = "ProfilesPreferences.Label")
public class ProfilesPreferences implements BasePreference<ProfilesPreferences> {

    @Property(bundleKey = "ProfilesPreferences.Profiles", 
            helpBundleKey= "ProfilesPreferences.Profiles.Help", 
            formType = PropertyFormType.COMBO)
    private Profile profiles;

    

    public ProfilesPreferences() {
    }
    
    public ProfilesPreferences(Profile profiles) {
        this.profiles = profiles;
    }

    @Override
    public ProfilesPreferences defaultValue(ProfilesPreferences defaultValue) {
        defaultValue.setProfiles(Profile.FULL);
        return defaultValue;
    }

    public Profile getProfiles() {
        return profiles;
    }

    public void setProfiles(Profile profiles) {
        this.profiles = profiles;
    }
   
}
