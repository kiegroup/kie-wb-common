/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.services.backend.allowlist;

import java.util.ArrayList;
import java.util.Set;

import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.shared.allowlist.PackageNameAllowListService;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.kie.workbench.common.services.shared.allowlist.AllowList;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Test splitting text into lines originating from different platforms.
 * <p/>
 * A line is considered to be terminated by any one of a line feed ('\n'),
 * a carriage return ('\r'), or a carriage return followed immediately by
 * a linefeed.
 * <p/>
 * See See https://en.wikipedia.org/wiki/Newline#Representations
 **/
@RunWith(MockitoJUnitRunner.Silent.class)
public class PackageNameAllowListServiceImplTest {

    @Mock
    PackageNameSearchProvider packageNameSearchProvider;

    @Mock
    PackageNameAllowListSaver saver;

    @Before
    public void setUp() throws Exception {
        when(packageNameSearchProvider.newTopLevelPackageNamesSearch(any(POM.class))).thenReturn(mock(PackageNameSearchProvider.PackageNameSearch.class));
    }

    @Test
    public void ifAllowListIsEmptyAllowListEverything() throws
            Exception {
        final PackageNameAllowListService packageNameAllowListService = makeService("");

        AllowList filterPackageNames = packageNameAllowListService.filterPackageNames(mock(KieModule.class),
                                                                                      new ArrayList<String>
                                                                                              () {{
                                                                                          add("a");
                                                                                          add("b");
                                                                                          add("c");
                                                                                      }});

        assertEquals(3,
                     filterPackageNames.size());
        assertTrue(filterPackageNames.contains("a"));
        assertTrue(filterPackageNames.contains("b"));
        assertTrue(filterPackageNames.contains("c"));
    }

    @Test
    public void testWindowsEncoding() {

        final PackageNameAllowListService packageNameAllowListService = makeService("a.**\r\nb\r\n");
        final Set<String> results = packageNameAllowListService.filterPackageNames(mock(KieModule.class),
                                                                                   new ArrayList<String>() {{
                                                                                       add("a");
                                                                                       add("b");
                                                                                       add("a.b");
                                                                                   }});
        assertEquals(3,
                     results.size());
        assertContains("a",
                       results);
        assertContains("b",
                       results);
        assertContains("a.b",
                       results);
    }

    @Test
    public void testSave() throws Exception {
        final PackageNameAllowListService service = makeService("");

        final Path path = mock(Path.class);
        final AllowList allowList = new AllowList();
        final Metadata metadata = new Metadata();
        final String comment = "comment";

        service.save(path,
                     allowList,
                     metadata,
                     comment);

        verify(saver).save(path,
                           allowList,
                           metadata,
                           comment);
    }

    @Test
    public void testUnixEncoding() {
        final PackageNameAllowListService packageNameAllowListService = makeService("a.**\nb\n");
        final Set<String> results = packageNameAllowListService.filterPackageNames(mock(KieModule.class),
                                                                                   new ArrayList<String>() {{
                                                                                       add("a");
                                                                                       add("b");
                                                                                       add("a.b");
                                                                                   }});
        assertEquals(3,
                     results.size());
        assertContains("a",
                       results);
        assertContains("b",
                       results);
        assertContains("a.b",
                       results);
    }

    private void assertContains(final String expected,
                                final Set<String> actual) {
        for (String a : actual) {
            if (expected.equals(a)) {
                return;
            }
        }
        fail("Expected pattern '" + expected + "' was not found in actual.");
    }

    private PackageNameAllowListService makeService(final String content) {
        return new PackageNameAllowListServiceImpl(mock(IOService.class),
                                                   mock(KieModuleService.class),
                                                   new PackageNameAllowListLoader(packageNameSearchProvider,
                                                                                  mock(IOService.class)) {
                                                       @Override
                                                       protected String loadContent(final Path packageNamesAllowListPath) {
                                                           return content;
                                                       }
                                                   },
                                                   saver);
    }
}