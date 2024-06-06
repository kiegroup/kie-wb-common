/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.library.api.preferences;

import org.uberfire.preferences.shared.PropertyFormOptions;
import org.uberfire.preferences.shared.annotations.Property;
import org.uberfire.preferences.shared.annotations.WorkbenchPreference;
import org.uberfire.preferences.shared.bean.BasePreference;
import org.uberfire.preferences.shared.impl.validation.NotEmptyValidator;
import org.uberfire.preferences.shared.impl.validation.NumberPropertyValidator;

@WorkbenchPreference(identifier = "LibraryProjectPreferences",
        bundleKey = "LibraryProjectPreferences.Label")
public class LibraryProjectPreferences implements BasePreference<LibraryProjectPreferences> {

    public static final String ASSETS_PER_PAGE_KEY = "org.kie.library.assets_per_page";
    public static final int ASSETS_PER_PAGE_VALUE = 15;

    @Property(bundleKey = "LibraryProjectPreferences.Version",
            helpBundleKey = "LibraryProjectPreferences.Version.Help",
            validators = {NotEmptyValidator.class, VersionValidator.class})
    String version;

    @Property(bundleKey = "LibraryProjectPreferences.Description",
            helpBundleKey = "LibraryProjectPreferences.Description.Help",
            validators = {DescriptionLengthValidator.class})
    String description;

    @Property(bundleKey = "LibraryProjectPreferences.Branch",
            helpBundleKey = "LibraryProjectPreferences.Branch.Help",
            formOptions = PropertyFormOptions.DISABLED)
    String branch;

    @Property(bundleKey = "LibraryProjectPreferences.AssetsPerPage",
            helpBundleKey = "LibraryProjectPreferences.AssetsPerPage.Help",
            validators = {NumberPropertyValidator.class})
    String assetsPerPage;

    public String getVersion() {
        return version;
    }

    public String getDescription() {
        return description;
    }

    public String getBranch() {
        return branch;
    }

    public int getAssetsPerPage() {
        String systemProperty = sdmSafeGetPropertyAssetsPerPage();

        int externalAssetsPerPage = 0;
        try {
            if (systemProperty.length() > 0) {
                externalAssetsPerPage = Integer.parseInt(systemProperty);
            }
            externalAssetsPerPage = externalAssetsPerPage > 0 ? externalAssetsPerPage : ASSETS_PER_PAGE_VALUE;

            if (assetsPerPage != null && !assetsPerPage.isEmpty()) {
                return Integer.parseInt(assetsPerPage);
            } else {
                return externalAssetsPerPage;
            }
        } catch (NumberFormatException e) {
            return externalAssetsPerPage;
        }
    }

    public static String sdmSafeGetPropertyAssetsPerPage() {
        //SUPER DEV MODE complains when calling System.getProperty using the constants directly.
        //GWT compilation fails and because of that some of the showcases won't work.
        //The variable assignment below prevents that.
        final String key = ASSETS_PER_PAGE_KEY;
        final String def = String.valueOf(LibraryProjectPreferences.ASSETS_PER_PAGE_VALUE);
        return System.getProperty(key, def);
    }
}
