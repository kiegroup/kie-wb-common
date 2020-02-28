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

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

import org.kie.workbench.common.stunner.bpmn.definition.BPMNDefinition;
import org.kie.workbench.common.stunner.bpmn.definition.DataObject;
import org.kie.workbench.common.stunner.bpmn.definition.property.artifacts.DataObjectType;
import org.kie.workbench.common.stunner.bpmn.definition.property.artifacts.DataObjectTypeValue;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.command.AddChildNodeCommand;
import org.kie.workbench.common.stunner.core.client.canvas.command.UpdateElementPropertyCommand;
import org.kie.workbench.common.stunner.core.client.canvas.event.command.CanvasCommandExecutedEvent;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.impl.CompositeCommand;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.uberfire.commons.services.cdi.Startup;

@Startup
@ApplicationScoped
public class DataObjectStateChecker {

    void onCommandExecuted(final @Observes CanvasCommandExecutedEvent commandExecutedEvent) {
        Command command = commandExecutedEvent.getCommand();

        if (command instanceof CompositeCommand) {
            onCompositeCommand(commandExecutedEvent, (CompositeCommand) command);
        } else if (command instanceof UpdateElementPropertyCommand) {
            onUpdateElementPropertyCommand(commandExecutedEvent, (UpdateElementPropertyCommand) command);
        }
    }

    private void onUpdateElementPropertyCommand(CanvasCommandExecutedEvent commandExecutedEvent, UpdateElementPropertyCommand command) {
        UpdateElementPropertyCommand propertyCommand = command;
        Element<View<?>> element = propertyCommand.getElement();
        if (null != element.asNode()) {
            Object bean = element.getContent().getDefinition();
            if (bean instanceof DataObject) {
                maybeUpdateTypes((DataObject) bean, commandExecutedEvent.getCanvasHandler());
            }
        }
    }

    private void onCompositeCommand(CanvasCommandExecutedEvent commandExecutedEvent, CompositeCommand command) {
        CompositeCommand cc = command;
        if (cc.size() == 2 && (cc.getCommands().get(0) instanceof AddChildNodeCommand)) {
            AddChildNodeCommand addChildNodeCommand = (AddChildNodeCommand) cc.getCommands().get(0);
            Element<View<?>> element = addChildNodeCommand.getCandidate();
            if (null != element.asNode()) {
                Object bean = element.getContent().getDefinition();
                if ((bean instanceof DataObject)) {
                    setTypeToNewNode(((DataObject) bean), commandExecutedEvent.getCanvasHandler());
                }
            }
        }
    }

    private void maybeUpdateTypes(DataObject dataObject, CanvasHandler canvasHandler) {
        Stream<DataObject> nodeStream = getDataObjectsAsStream(canvasHandler);
        nodeStream.forEach(candidate -> {
            if (candidate.getName().getValue().equals(dataObject.getName().getValue())) {
                candidate.getType().getValue().setType(dataObject.getType().getValue().getType());
            }
        });
    }

    private Stream<DataObject> getDataObjectsAsStream(CanvasHandler canvasHandler) {
        Iterable<Node> nodes = canvasHandler
                .getDiagram()
                .getGraph()
                .nodes();

        return StreamSupport.stream(nodes.spliterator(), false)
                .filter(this::isBPMNDefinition)
                .map(elm -> (Node<View<BPMNDefinition>, Edge>) elm)
                .filter(elm -> elm.getContent().getDefinition() instanceof DataObject)
                .map(elm -> (DataObject) elm.getContent().getDefinition());
    }

    private boolean isBPMNDefinition(Node node) {
        return node.getContent() instanceof View &&
                ((View) node.getContent()).getDefinition() instanceof BPMNDefinition;
    }

    private void setTypeToNewNode(DataObject dataObject, CanvasHandler canvasHandler) {
        getDataObjectsAsStream(canvasHandler)
                .filter(elm -> !dataObject.equals(elm))
                .findFirst()
                .ifPresent(elm -> dataObject.setType(new DataObjectType(
                        new DataObjectTypeValue(elm.getType().getValue().getType()))));
    }
}
