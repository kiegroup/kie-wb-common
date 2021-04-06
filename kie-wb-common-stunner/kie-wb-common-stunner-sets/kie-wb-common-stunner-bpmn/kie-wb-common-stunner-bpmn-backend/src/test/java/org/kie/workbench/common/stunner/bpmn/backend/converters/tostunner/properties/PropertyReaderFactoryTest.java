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

package org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties;

import java.util.Collections;

import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.Process;
import org.eclipse.bpmn2.ServiceTask;
import org.eclipse.bpmn2.di.BPMNDiagram;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.DefinitionResolver;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.Factories.bpmn2;
import static org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.Factories.di;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PropertyReaderFactoryTest {

    private Definitions definitions;

    private DefinitionResolver definitionResolver;

    private Process process;

    private PropertyReaderFactory tested;

    @Mock
    private ServiceTask task;

    @Mock
    private FeatureMap attributes;

    @Before
    public void setUp() {
        definitions = bpmn2.createDefinitions();
        process = bpmn2.createProcess();
        definitions.getRootElements().add(process);
        BPMNDiagram bpmnDiagram = di.createBPMNDiagram();
        bpmnDiagram.setPlane(di.createBPMNPlane());
        definitions.getDiagrams().add(bpmnDiagram);

        definitionResolver = new DefinitionResolver(definitions, Collections.emptyList());

        tested = new PropertyReaderFactory(definitionResolver);
    }

    @Test
    public void ofDefinition() {
        assertTrue(tested.of(definitions) instanceof DefinitionsPropertyReader);
    }

    @Test
    public void testOfCustom() {
        when(task.getName()).thenReturn("MyCustomTask");
        when(task.getAnyAttribute()).thenReturn(attributes);

        final ServiceTaskPropertyReader serviceTaskPropertyReader = tested.ofCustom(task);

        assertSame(serviceTaskPropertyReader.task, task);
        assertEquals(serviceTaskPropertyReader.getName(), task.getName());
        verify(task).getAnyAttribute();
    }
}