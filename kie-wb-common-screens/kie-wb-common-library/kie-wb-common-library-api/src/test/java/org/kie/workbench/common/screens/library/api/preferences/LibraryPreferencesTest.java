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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.backend.validation.PackageNameValidator;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest({LibraryProjectPreferences.class})
public class LibraryPreferencesTest {

    private LibraryPreferences libraryPreferences;

    private PackageNameValidator packageNameValidator;

    @Before
    public void setup() {
        libraryPreferences = new LibraryPreferences();
        libraryPreferences.organizationalUnitPreferences = new LibraryOrganizationalUnitPreferences();
        libraryPreferences.projectPreferences = new LibraryProjectPreferences();

        packageNameValidator = new PackageNameValidator();
    }

    @Test
    public void organizationalUnitGroupIdIsAValidPackageTest() {
        libraryPreferences = libraryPreferences.defaultValue(libraryPreferences);
        assertTrue(packageNameValidator.isValid(libraryPreferences.getOrganizationalUnitPreferences().getGroupId()));
    }

    @Test
    public void defaultValueTest() {
        final String expected = String.valueOf(LibraryProjectPreferences.ASSETS_PER_PAGE_VALUE);
        mockStatic(LibraryProjectPreferences.class);
        when(LibraryProjectPreferences.sdmSafeGetPropertyAssetsPerPage()).thenReturn(expected);

        LibraryPreferences libraryPreferences = new LibraryPreferences();
        libraryPreferences.organizationalUnitPreferences = new LibraryOrganizationalUnitPreferences();
        libraryPreferences.projectPreferences = new LibraryProjectPreferences();

        libraryPreferences = libraryPreferences.defaultValue(libraryPreferences);

        final String result = libraryPreferences.projectPreferences.assetsPerPage;

        verifyStatic(LibraryProjectPreferences.class);
        LibraryProjectPreferences.sdmSafeGetPropertyAssetsPerPage();

        assertEquals(expected, result);
    }
}
