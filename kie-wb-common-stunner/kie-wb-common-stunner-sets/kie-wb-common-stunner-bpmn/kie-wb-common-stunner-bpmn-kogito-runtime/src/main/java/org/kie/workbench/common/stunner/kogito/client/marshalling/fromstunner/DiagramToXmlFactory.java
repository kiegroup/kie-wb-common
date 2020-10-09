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

package org.kie.workbench.common.stunner.kogito.client.marshalling.fromstunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.xml.stream.XMLStreamException;

import org.kie.workbench.common.stunner.bpmn.definition.BPMNDiagramImpl;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNViewDefinition;
import org.kie.workbench.common.stunner.bpmn.definition.UserTask;
import org.kie.workbench.common.stunner.bpmn.definition.dto.Definitions;
import org.kie.workbench.common.stunner.bpmn.definition.dto.Definitions_XMLMapperImpl;
import org.kie.workbench.common.stunner.bpmn.definition.dto.Import;
import org.kie.workbench.common.stunner.bpmn.definition.dto.ItemDefinition;
import org.kie.workbench.common.stunner.bpmn.definition.dto.Property;
import org.kie.workbench.common.stunner.bpmn.definition.dto.Relationship;
import org.kie.workbench.common.stunner.bpmn.definition.dto.bpmndi.BPMNPlane;
import org.kie.workbench.common.stunner.bpmn.definition.dto.bpmndi.BPMNPlaneElement;
import org.kie.workbench.common.stunner.bpmn.definition.dto.bpmndi.BPMNShape;
import org.kie.workbench.common.stunner.bpmn.definition.dto.bpsim.Availability;
import org.kie.workbench.common.stunner.bpmn.definition.dto.bpsim.BPSimData;
import org.kie.workbench.common.stunner.bpmn.definition.dto.bpsim.CostParameters;
import org.kie.workbench.common.stunner.bpmn.definition.dto.bpsim.ElementParameters;
import org.kie.workbench.common.stunner.bpmn.definition.dto.bpsim.FloatingParameter;
import org.kie.workbench.common.stunner.bpmn.definition.dto.bpsim.NormalDistribution;
import org.kie.workbench.common.stunner.bpmn.definition.dto.bpsim.ProcessingTime;
import org.kie.workbench.common.stunner.bpmn.definition.dto.bpsim.Quantity;
import org.kie.workbench.common.stunner.bpmn.definition.dto.bpsim.ResourceParameters;
import org.kie.workbench.common.stunner.bpmn.definition.dto.bpsim.Scenario;
import org.kie.workbench.common.stunner.bpmn.definition.dto.bpsim.TimeParameters;
import org.kie.workbench.common.stunner.bpmn.definition.dto.bpsim.UnitCost;
import org.kie.workbench.common.stunner.bpmn.definition.dto.dc.Bounds;
import org.kie.workbench.common.stunner.bpmn.definition.dto.drools.ExtensionElement;
import org.kie.workbench.common.stunner.bpmn.definition.dto.drools.Global;
import org.kie.workbench.common.stunner.bpmn.definition.dto.drools.MetaData;
import org.kie.workbench.common.stunner.bpmn.definition.property.variables.ProcessVariableSerializer;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewImpl;
import org.kie.workbench.common.stunner.core.graph.impl.NodeImpl;
import org.treblereel.gwt.jackson.api.DefaultXMLSerializationContext;
import org.treblereel.gwt.jackson.api.XMLSerializationContext;

public class DiagramToXmlFactory {

    private final Diagram diagram;
    private final Definitions definitions;
    private final List<Import> imports = new ArrayList<>();
    private final List<ElementParameters> elementParameters = new ArrayList<>();
    private final List<BPMNPlaneElement> elements = new ArrayList<>();
    private final List<BPMNViewDefinition> definitionList = new LinkedList<>();
    private final List<Property> properties = new LinkedList<>();
    private final Set<ExtensionElement> extensionElements = new LinkedHashSet<>();
    private final LinkedList<ItemDefinition> itemDefinitions = new LinkedList<>();

    public DiagramToXmlFactory(Diagram diagram) {
        this.diagram = diagram;
        this.definitions = new Definitions();
    }

    public String toXml() {
        String definitionsId = diagram.getMetadata().getCanvasRootUUID();
        definitions.setId(definitionsId);
        definitions.setImports(imports);
        definitions.setItemDefinitions(itemDefinitions);

        setRelationship(definitions);

        BPMNPlane plane = new BPMNPlane();
        plane.setElements(elements);

        diagram.getGraph().nodes().forEach(n -> {
            NodeImpl elm = (NodeImpl) n;
            Node node = elm.asNode();
            if (node.getContent() instanceof ViewImpl) {
                ViewImpl view = (ViewImpl) elm.asNode().getContent();
                if (view.getDefinition() instanceof UserTask) {
                    ((UserTask) view.getDefinition()).setId(node.getUUID());
                    addUserTask(view);
                } else if (view.getDefinition() instanceof BPMNDiagramImpl) {
                    addDiagram(definitionsId, plane, view);
                }
            }
        });

        setProcessVariables();
        setCustomProperties();
        setImports();
        setGlobal();

        try {
            XMLSerializationContext context = DefaultXMLSerializationContext.builder()
                    .serializeNulls(false)
                    .writeEmptyXMLArrays(false).build();

            return Definitions_XMLMapperImpl.INSTANCE.write(definitions, context);
        } catch (XMLStreamException e) {
            e.printStackTrace();
            return "";
        }
    }

    private void setRelationship(Definitions definitions) {
        definitions.setRelationship(new Relationship());
        definitions.getRelationship().setSource(definitions.getId());
        definitions.getRelationship().setTarget(definitions.getId());
        definitions.getRelationship().setExtensionElements(new ArrayList<>());

        Scenario scenario = new Scenario(elementParameters);
        definitions.getRelationship().getExtensionElements().add(new BPSimData(scenario));
    }

    private void addUserTask(ViewImpl view) {
        UserTask userTask = (UserTask) view.getDefinition();
        new UserTaskConverter(userTask, definitions).convert();
        BPMNShape shape = new BPMNShape(userTask.getId());
        shape.setBounds(new Bounds(view.getBounds()));
        elements.add(shape);

        ElementParameters parameters = new ElementParameters(userTask.getId());
        elementParameters.add(parameters);

        parameters.setParameters(new ArrayList<>());
        parameters.getParameters().add(new TimeParameters(new ProcessingTime(new NormalDistribution())));
        parameters.getParameters().add(new ResourceParameters(new Availability(new FloatingParameter()), new Quantity(new FloatingParameter())));
        parameters.getParameters().add(new CostParameters(new UnitCost(new FloatingParameter())));
        definitionList.add(userTask);
    }

    private void addDiagram(String definitionsId,
                            BPMNPlane plane,
                            ViewImpl view) {
        BPMNDiagramImpl bpmnDiagram = (BPMNDiagramImpl) view.getDefinition();
        definitions.setProcess(bpmnDiagram.getDiagramSet());
        definitions.getProcess().setDefinitionList(definitionList);
        definitions.getProcess().setExtensionElements(extensionElements);
        definitions.getProcess().setProperties(properties);
        definitions.getRelationship().setSource(definitionsId);
        definitions.getRelationship().setTarget(definitionsId);
        definitions.setBpmnDiagram(bpmnDiagram);

        plane.setBpmnElement(definitions.getProcess().getId().getValue());
        bpmnDiagram.setPlane(plane);
    }

    private void setProcessVariables() {
        ProcessVariableSerializer.deserialize(definitions
                                                      .getBpmnDiagram()
                                                      .getProcessData()
                                                      .getProcessVariables().getValue())
                .forEach((name, variableInfo) -> {
                    properties.add(new Property(name, variableInfo.tags));
                    itemDefinitions.addFirst(new ItemDefinition()
                                                     .setId("_" + name + "Item")
                                                     .setStructureRef(variableInfo.type));
                });
    }

    private void setCustomProperties() {
        if (!definitions.getProcess().getSlaDueDate().getValue().isEmpty()) {
            definitions.getProcess()
                    .getExtensionElements()
                    .add(new MetaData("customSLADueDate", definitions.getProcess().getSlaDueDate().getValue()));
        }

        if (!definitions.getProcess().getProcessInstanceDescription().getValue().isEmpty()) {
            definitions.getProcess()
                    .getExtensionElements()
                    .add(new MetaData("customDescription", definitions.getProcess().getProcessInstanceDescription().getValue()));
        }
    }

    private void setImports() {
        definitions.getProcess().getImports()
                .getValue()
                .getWSDLImports()
                .forEach(wsdlImport -> definitions.getImports()
                        .add(new Import(wsdlImport.getLocation(), wsdlImport.getNamespace())));

        definitions.getProcess().getImports()
                .getValue()
                .getDefaultImports().forEach(defaultImport -> {
            definitions.getProcess().getExtensionElements().add(
                    new org.kie.workbench.common.stunner.bpmn.definition.dto.drools.Import(defaultImport.getClassName()));
        });
    }

    private void setGlobal() {
        String globals = definitions
                .getBpmnDiagram()
                .getAdvancedData()
                .getGlobalVariables().getValue();
        if (!globals.isEmpty()) {
            String[] arr = globals.split(",");
            Arrays.stream(arr).forEach(elm -> {
                String[] global = elm.split(":");
                definitions.getProcess()
                        .getExtensionElements().add(new Global(global[0], global[1]));
            });
        }
    }
}
