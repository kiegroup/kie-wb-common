/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
 */

package org.kie.workbench.common.dmn.api.definition.v1_1;

import java.util.Optional;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DMNModelInstrumentedBaseTest {

    private static final String DUMMY_URI = "http://http://www.kiegroup.org";

    @Test
    public void testParent() {
        final MockDMNModelClass parent = new MockDMNModelClass();
        final MockDMNModelClass child = new MockDMNModelClass();

        child.setParent(parent);

        assertEquals(parent,
                     child.getParent());
    }

    @Test
    public void testGetPrefixForNamespaceURIInheritance() {
        final MockDMNModelClass parent = new MockDMNModelClass();
        final MockDMNModelClass child = new MockDMNModelClass();

        parent.getNsContext().put(DMNModelInstrumentedBase.PREFIX_FEEL,
                                  DMNModelInstrumentedBase.URI_FEEL);

        child.setParent(parent);

        final Optional<String> parentFeelPrefix = parent.getPrefixForNamespaceURI(DMNModelInstrumentedBase.URI_FEEL);
        assertTrue(parentFeelPrefix.isPresent());
        assertEquals(DMNModelInstrumentedBase.PREFIX_FEEL,
                     parentFeelPrefix.get());

        final Optional<String> childFeelPrefix = child.getPrefixForNamespaceURI(DMNModelInstrumentedBase.URI_FEEL);
        assertTrue(childFeelPrefix.isPresent());
        assertEquals(DMNModelInstrumentedBase.PREFIX_FEEL,
                     childFeelPrefix.get());
    }

    @Test
    public void testGetPrefixForNamespaceURIOverride() {
        final MockDMNModelClass parent = new MockDMNModelClass();
        final MockDMNModelClass child = new MockDMNModelClass();

        parent.getNsContext().put(DMNModelInstrumentedBase.PREFIX_FEEL,
                                  DMNModelInstrumentedBase.URI_FEEL);
        child.getNsContext().put(DMNModelInstrumentedBase.PREFIX_FEEL,
                                 DUMMY_URI);

        child.setParent(parent);

        final Optional<String> parentFeelPrefix = parent.getPrefixForNamespaceURI(DMNModelInstrumentedBase.URI_FEEL);
        assertTrue(parentFeelPrefix.isPresent());
        assertEquals(DMNModelInstrumentedBase.PREFIX_FEEL,
                     parentFeelPrefix.get());

        final Optional<String> childFeelPrefix = child.getPrefixForNamespaceURI(DUMMY_URI);
        assertTrue(childFeelPrefix.isPresent());
        assertEquals(DMNModelInstrumentedBase.PREFIX_FEEL,
                     childFeelPrefix.get());
    }

    public class MockDMNModelClass extends DMNModelInstrumentedBase {
        //Nothing to add!
    }
}
