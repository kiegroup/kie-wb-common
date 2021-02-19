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

package org.kie.workbench.common.stunner.kogito.client.marshalling.tostunner;

import java.util.function.Function;

import javax.xml.stream.XMLStreamException;

import org.kie.workbench.common.stunner.bpmn.definition.BPMNDiagramImpl;
import org.kie.workbench.common.stunner.bpmn.definition.UserTask;
import org.kie.workbench.common.stunner.bpmn.definition.dto.Definitions;
import org.kie.workbench.common.stunner.bpmn.definition.dto.Definitions_XMLMapperImpl;
import org.kie.workbench.common.stunner.bpmn.definition.dto.ItemDefinition;
import org.kie.workbench.common.stunner.bpmn.definition.dto.bpmndi.BPMNPlaneElement;
import org.kie.workbench.common.stunner.bpmn.definition.dto.bpmndi.BPMNShape;
import org.kie.workbench.common.stunner.bpmn.definition.dto.drools.Import;
import org.kie.workbench.common.stunner.bpmn.definition.dto.drools.MetaData;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.imports.DefaultImport;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.imports.WSDLImport;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Child;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewImpl;
import org.kie.workbench.common.stunner.core.graph.impl.EdgeImpl;
import org.kie.workbench.common.stunner.core.graph.impl.NodeImpl;
import org.kie.workbench.common.stunner.core.util.UUID;
import org.treblereel.gwt.xml.mapper.api.DefaultXMLDeserializationContext;
import org.treblereel.gwt.xml.mapper.api.XMLDeserializationContext;

public class XmlToDiagramFactory {

    private final Diagram diagram;
    private final Definitions definitions;
    private final Function<String, ItemDefinition> getItemDefinitionStructureRef = new Function<String, ItemDefinition>() {
        @Override
        public ItemDefinition apply(String id) {
            return definitions.getItemDefinitions()
                    .stream()
                    .filter(item -> item.getId().equals(id))
                    .findFirst().orElseThrow(() -> new Error("Unable to fetch ItemDefinition with id :" + id));
        }
    };

    public XmlToDiagramFactory(String xml, Diagram diagram) {
        this.diagram = diagram;
        try {
            XMLDeserializationContext context = DefaultXMLDeserializationContext.builder().failOnUnknownProperties(false).build();
            definitions = Definitions_XMLMapperImpl.INSTANCE.read(xml, context);
        } catch (XMLStreamException e) {
            throw new Error("Unable to process xml \n" + xml);
        }
    }

    public void process() {

        NodeImpl element = (NodeImpl) diagram.getGraph().nodes().iterator().next();
        Node rootNode = element.asNode();

        BPMNDiagramImpl bpmnDiagram = (BPMNDiagramImpl) ((View) rootNode.getContent()).getDefinition();

        bpmnDiagram.setDiagramSet(definitions.getProcess());

        addGlobal(definitions, bpmnDiagram);
        addMetaData(definitions, bpmnDiagram);
        addProcessVariables(definitions, bpmnDiagram);
        addDefaultImport(definitions, bpmnDiagram);
        addWSDLImport(definitions, bpmnDiagram);
        addCustomProperties(definitions, bpmnDiagram);

        if (definitions.getProcess().getDefinitionList() != null) {
            definitions.getProcess().getDefinitionList().forEach(definition -> {
                if (definition instanceof UserTask) {
                    UserTask userTask = (UserTask) definition;
                    String id = userTask.getId();

                    new UserTaskConverter(userTask, definitions).convert();

                    BPMNPlaneElement planeElement = definitions.getBpmnDiagram().getPlane()
                            .getElements()
                            .stream()
                            .filter(elm -> elm.getId().equals("shape_" + id))
                            .findFirst()
                            .orElseThrow(() -> new Error("Unable to find a Bound " + id));

                    org.kie.workbench.common.stunner.bpmn.definition.dto.dc.Bounds bounds = ((BPMNShape) planeElement).getBounds();

                    Node<View, Edge> node = newNode(id, definition, Bounds.create(bounds.getX(),
                                                                                  bounds.getY(),
                                                                                  bounds.getX() + bounds.getWidth(),
                                                                                  bounds.getY() + bounds.getHeight()));
                    node.getLabels().addAll(userTask.getLabels());

                    final Edge<Child, Node> edge = new EdgeImpl<>(UUID.uuid());
                    edge.setSourceNode(rootNode);
                    edge.setTargetNode(node);
                    edge.setContent(new Child());

                    node.getInEdges().add(edge);
                    rootNode.getOutEdges().add(edge);
                    diagram.getGraph().addNode(node);
                }
            });
        }
    }

    private void addGlobal(Definitions definitions, BPMNDiagramImpl bpmnDiagram) {
        new GlobalVariablesElement(definitions)
                .getStringValue()
                .ifPresent(val -> bpmnDiagram.getAdvancedData()
                        .getGlobalVariables()
                        .setValue(val));
    }

    private void addMetaData(Definitions definitions, BPMNDiagramImpl bpmnDiagram) {
        if (definitions.getProcess() != null
                && definitions.getProcess().getExtensionElements() != null) {
            new MetaDataAttributesElement(definitions).getStringValue().ifPresent(val -> {
                bpmnDiagram.getAdvancedData().getMetaDataAttributes().setValue(val);
            });
        }
    }

    private void addProcessVariables(Definitions definitions, BPMNDiagramImpl bpmnDiagram) {
        if (definitions.getProcess() != null
                && definitions.getProcess().getProperties() != null) {
            bpmnDiagram.getProcessData()
                    .getProcessVariables()
                    .setValue(ProcessVariableReader
                                      .getProcessVariables(definitions, getItemDefinitionStructureRef));
        }
    }

    private void addDefaultImport(Definitions definitions, BPMNDiagramImpl bpmnDiagram) {
        if (definitions.getProcess() != null
                && definitions.getProcess().getExtensionElements() != null) {
            definitions.getProcess()
                    .getExtensionElements()
                    .stream()
                    .filter(extensionElement -> extensionElement instanceof Import)
                    .map(imp -> new DefaultImport(imp.getName()))
                    .forEach(def -> bpmnDiagram.getDiagramSet()
                            .getImports()
                            .getValue()
                            .getDefaultImports()
                            .add(def));
        }
    }

    private void addWSDLImport(Definitions definitions, BPMNDiagramImpl bpmnDiagram) {
        if (definitions.getImports() != null) {
            definitions.getImports()
                    .stream()
                    .map(imp -> new WSDLImport(imp.getLocation(), imp.getNamespace()))
                    .forEach(imp -> bpmnDiagram.getDiagramSet().getImports().getValue().getWSDLImports().add(imp));
        }
    }

    private void addCustomProperties(Definitions definitions, BPMNDiagramImpl bpmnDiagram) {
        if (definitions.getProcess() != null
                && definitions.getProcess().getExtensionElements() != null) {
            definitions.getProcess().getExtensionElements().stream()
                    .filter(extensionElement -> extensionElement instanceof MetaData)
                    .map(elm -> (MetaData) elm)
                    .forEach(elm -> {
                        if (elm.getName().equals("customSLADueDate")) {
                            definitions.getProcess().getSlaDueDate().setValue(elm.getMetaValue().getValue());
                        } else if (elm.getName().equals("customDescription")) {
                            definitions.getProcess().getProcessInstanceDescription().setValue(elm.getMetaValue().getValue());
                        }
                    });
        }
    }

    private Node<View, Edge> newNode(String id, Object definition, Bounds bounds) {
        final Node<View, Edge> node = new NodeImpl<>(id);
        final View<Object> content = new ViewImpl<>(definition, bounds);
        node.setContent(content);
        return node;
    }
}
