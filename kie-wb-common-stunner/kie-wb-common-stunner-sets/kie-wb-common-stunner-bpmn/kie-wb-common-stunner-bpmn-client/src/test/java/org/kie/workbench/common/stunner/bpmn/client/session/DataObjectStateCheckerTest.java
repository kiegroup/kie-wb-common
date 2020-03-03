/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.client.session;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.definition.DataObject;
import org.kie.workbench.common.stunner.bpmn.definition.property.artifacts.DataObjectName;
import org.kie.workbench.common.stunner.bpmn.definition.property.artifacts.DataObjectType;
import org.kie.workbench.common.stunner.bpmn.definition.property.artifacts.DataObjectTypeValue;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.command.AddChildNodeCommand;
import org.kie.workbench.common.stunner.core.client.canvas.command.UpdateElementPropertyCommand;
import org.kie.workbench.common.stunner.core.client.canvas.event.command.CanvasCommandExecutedEvent;
import org.kie.workbench.common.stunner.core.command.impl.CompositeCommand;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DataObjectStateCheckerTest {

    @Mock
    private CanvasHandler canvasHandler;
    private DataObjectStateChecker tested = new DataObjectStateChecker();
    @Mock
    private Element<View<?>> element;
    @Mock
    private View view;
    @Mock
    private Diagram diagram;
    @Mock
    private Graph graph;
    @Mock
    private Node node;
    private List<Node> nodes;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        when(canvasHandler.getDiagram()).thenReturn(diagram);
        when(diagram.getGraph()).thenReturn(graph);
        when(element.asNode()).thenReturn(node);
        when(element.getContent()).thenReturn(view);
    }

    @Test
    public void testUpdateElementPropertyCommand() {
        nodes = new ArrayList<>();

        DataObject do1 = createDataObject("DO1", "TYPE2");
        DataObject do2 = createDataObject("DO1", "TYPE1");
        DataObject do3 = createDataObject("DO1", "TYPE1");
        DataObject do4 = createDataObject("DO3", "TYPE1");

        nodes.add(mockNode(do1));
        nodes.add(mockNode(do2));
        nodes.add(mockNode(do3));
        nodes.add(mockNode(do4));

        when(graph.nodes()).thenReturn(nodes);
        when(view.getDefinition()).thenReturn(do1);

        CanvasCommandExecutedEvent command = new CanvasCommandExecutedEvent(canvasHandler,
                                                                            new UpdateElementPropertyCommand(element, "any", "one"),
                                                                            null);
        tested.onCommandExecuted(command);

        Assert.assertEquals("DO1", do1.getDataObjectName().getValue());
        Assert.assertEquals("TYPE2", do1.getType().getValue().getType());

        Assert.assertEquals("DO1", do2.getDataObjectName().getValue());
        Assert.assertEquals("TYPE2", do2.getType().getValue().getType());

        Assert.assertEquals("DO1", do3.getDataObjectName().getValue());
        Assert.assertEquals("TYPE2", do3.getType().getValue().getType());

        Assert.assertEquals("DO3", do4.getDataObjectName().getValue());
        Assert.assertEquals("TYPE1", do4.getType().getValue().getType());
    }

    private DataObject createDataObject(String name, String type) {
        DataObject dataObject = new DataObject();
        dataObject.setDataObjectName(new DataObjectName(name));
        dataObject.setType(new DataObjectType(new DataObjectTypeValue(type)));
        return dataObject;
    }

    protected Node mockNode(Object definition) {
        Node node = mock(Node.class);
        View view = mock(View.class);
        when(node.getContent()).thenReturn(view);
        when(view.getDefinition()).thenReturn(definition);
        when(node.asNode()).thenReturn(node);
        return node;
    }

    @Test
    public void testCompositeCommand() {
        nodes = new ArrayList<>();
        when(graph.nodes()).thenReturn(nodes);

        DataObject do1 = createDataObject("DataObjectName1", "DDD");
        DataObject test = createDataObject("DataObjectName", "TYPE1");

        nodes.add(mockNode(do1));

        AddChildNodeCommand addChildNodeCommand = new AddChildNodeCommand(null, mockNode(test), "");
        CompositeCommand command = new CompositeCommand(false);
        command.addCommand(addChildNodeCommand);
        command.addCommand(addChildNodeCommand);

        CanvasCommandExecutedEvent event = new CanvasCommandExecutedEvent(canvasHandler,
                                                                          command,
                                                                          null);
        tested.onCommandExecuted(event);

        Assert.assertEquals("TYPE1", test.getType().getValue().getType());

        nodes.add(mockNode(test));
        DataObject test2 = createDataObject("DataObjectName", "TYPE3");

        addChildNodeCommand = new AddChildNodeCommand(null, mockNode(test2), "");
        command = new CompositeCommand(false);
        command.addCommand(addChildNodeCommand);
        command.addCommand(addChildNodeCommand);

        event = new CanvasCommandExecutedEvent(canvasHandler,
                                               command,
                                               null);
        tested.onCommandExecuted(event);
        Assert.assertEquals("TYPE1", test2.getType().getValue().getType());
    }
}
