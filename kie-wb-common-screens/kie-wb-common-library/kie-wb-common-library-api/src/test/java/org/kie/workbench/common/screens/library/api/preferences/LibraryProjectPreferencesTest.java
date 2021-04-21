/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 *
 */

package org.kie.workbench.common.screens.library.api.preferences;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class LibraryProjectPreferencesTest {

    private LibraryProjectPreferences libraryProjectPreferences;

    @Before
    public void setUp() {
        libraryProjectPreferences = new LibraryProjectPreferences();
        System.setProperty(LibraryProjectPreferences.ASSETS_PER_PAGE_KEY, "");
    }

    @After
    public void tearDown() {
        System.setProperty(LibraryProjectPreferences.ASSETS_PER_PAGE_KEY, "");
    }

    @Test
    public void testInvalidStoredValue() {
        libraryProjectPreferences.assetsPerPage = "not a number";
        int pageSize = libraryProjectPreferences.getAssetsPerPage();
        assertEquals(15, pageSize);
    }

    @Test
    public void testValidStoredValue() {
        libraryProjectPreferences.assetsPerPage = "17";
        int pageSize = libraryProjectPreferences.getAssetsPerPage();
        assertEquals(17, pageSize);
    }

    @Test
    public void testInvalidStoredValueWithSystemProperty() {
        System.setProperty(LibraryProjectPreferences.ASSETS_PER_PAGE_KEY, "37");
        libraryProjectPreferences.assetsPerPage = "not a number";
        int pageSize = libraryProjectPreferences.getAssetsPerPage();
        assertEquals(37, pageSize);
    }
}