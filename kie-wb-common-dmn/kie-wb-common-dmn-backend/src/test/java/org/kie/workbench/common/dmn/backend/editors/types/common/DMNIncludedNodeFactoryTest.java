/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.backend.editors.types.common;

import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.dmn.api.definition.v1_1.DRGElement;
import org.kie.workbench.common.dmn.api.definition.v1_1.Decision;
import org.kie.workbench.common.dmn.api.editors.types.DMNIncludedNode;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.uberfire.backend.vfs.Path;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DMNIncludedNodeFactoryTest {

    private DMNIncludedNodeFactory factory;

    @Before
    public void setup() {
        factory = new DMNIncludedNodeFactory();
    }

    @Test
    public void testMakeDMNIncludeModel() {

        final Path path = mock(Path.class);
        final String id = "0000-1111-3333-4444";
        final String name = "Can Drive?";
        final String fileName = "file.dmn";
        final DRGElement drgElement = makeDecision(id, name);

        when(path.getFileName()).thenReturn(fileName);

        final DMNIncludedNode node = factory.makeDMNIncludeModel(path, drgElement);

        assertEquals(id, node.getDrgElementId());
        assertEquals(name, node.getDrgElementName());
        assertEquals(fileName, node.getModelName());
        assertEquals(Decision.class, node.getDrgElementClass());
    }

    private Decision makeDecision(final String id,
                                  final String name) {
        final Decision decision = new Decision();
        decision.setId(new Id(id));
        decision.setName(new Name(name));
        return decision;
    }
}
