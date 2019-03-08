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

package org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.activities;

import java.util.UUID;

import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.backend.converters.customproperties.CustomElement;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties.CallActivityPropertyWriter;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties.PropertyWriter;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties.PropertyWriterFactory;
import org.kie.workbench.common.stunner.bpmn.definition.BaseReusableSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.ReusableSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.property.subProcess.IsCase;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.AdHocAutostart;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewImpl;
import org.kie.workbench.common.stunner.core.graph.impl.NodeImpl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ReusableSubprocessConverterTest {

    private ReusableSubprocessConverter tested =
            new ReusableSubprocessConverter(new PropertyWriterFactory());

    @Test
    public void testToFlowElement_case() throws Exception {
        final BaseReusableSubprocess definition = new ReusableSubprocess();
        definition.getExecutionSet().setIsCase(new IsCase(true));
        final View<BaseReusableSubprocess> view = new ViewImpl<>(definition, Bounds.create());
        final Node<View<BaseReusableSubprocess>, ?> node = new NodeImpl<>(UUID.randomUUID().toString());
        node.setContent(view);

        final PropertyWriter propertyWriter = tested.toFlowElement(node);
        assertTrue(CallActivityPropertyWriter.class.isInstance(propertyWriter));
        assertTrue(CustomElement.isCase.of(propertyWriter.getFlowElement()).get());
    }

    @Test
    public void testToFlowElement_process() throws Exception {
        final BaseReusableSubprocess definition = new ReusableSubprocess();
        final View<BaseReusableSubprocess> view = new ViewImpl<>(definition, Bounds.create());
        final Node<View<BaseReusableSubprocess>, ?> node = new NodeImpl<>(UUID.randomUUID().toString());
        node.setContent(view);

        final PropertyWriter propertyWriter = tested.toFlowElement(node);
        assertTrue(CallActivityPropertyWriter.class.isInstance(propertyWriter));
        assertFalse(CustomElement.isCase.of(propertyWriter.getFlowElement()).get());
    }

    @Test
    public void testToFlowElement_autostart() throws Exception {
        final ReusableSubprocess definition = new ReusableSubprocess();
        definition.getExecutionSet().setAdHocAutostart(new AdHocAutostart(true));
        final View<BaseReusableSubprocess> view = new ViewImpl<>(definition, Bounds.create());
        final Node<View<BaseReusableSubprocess>, ?> node = new NodeImpl<>(UUID.randomUUID().toString());
        node.setContent(view);

        final PropertyWriter propertyWriter = tested.toFlowElement(node);
        assertTrue(CallActivityPropertyWriter.class.isInstance(propertyWriter));
        assertTrue(CustomElement.autoStart.of(propertyWriter.getFlowElement()).get());
    }

    @Test
    public void testToFlowElement_notautostart() throws Exception {
        final ReusableSubprocess definition = new ReusableSubprocess();
        definition.getExecutionSet().setAdHocAutostart(new AdHocAutostart(false));
        final View<BaseReusableSubprocess> view = new ViewImpl<>(definition, Bounds.create());
        final Node<View<BaseReusableSubprocess>, ?> node = new NodeImpl<>(UUID.randomUUID().toString());
        node.setContent(view);

        final PropertyWriter propertyWriter = tested.toFlowElement(node);
        assertTrue(CallActivityPropertyWriter.class.isInstance(propertyWriter));
        assertFalse(CustomElement.autoStart.of(propertyWriter.getFlowElement()).get());
    }
}