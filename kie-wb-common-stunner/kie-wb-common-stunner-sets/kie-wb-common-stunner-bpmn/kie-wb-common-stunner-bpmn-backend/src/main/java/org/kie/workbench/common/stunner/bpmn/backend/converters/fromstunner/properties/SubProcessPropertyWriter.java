package org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bpsim.ElementParameters;
import org.eclipse.bpmn2.Documentation;
import org.eclipse.bpmn2.LaneSet;
import org.eclipse.bpmn2.Property;
import org.eclipse.bpmn2.SubProcess;
import org.eclipse.bpmn2.di.BPMNEdge;
import org.kie.workbench.common.stunner.bpmn.backend.converters.customproperties.CustomElement;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.ElementContainer;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties.SimulationSets;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.DeclarationList;
import org.kie.workbench.common.stunner.bpmn.definition.property.simulation.SimulationSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.variables.ProcessVariables;

import static org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.Factories.bpmn2;

public class SubProcessPropertyWriter extends PropertyWriter implements ElementContainer {

    private final SubProcess process;
    private Collection<ElementParameters> simulationParameters = new ArrayList<>();
    private Map<String, BasePropertyWriter> childElements = new HashMap<>();

    public SubProcessPropertyWriter(SubProcess process, VariableScope variableScope) {
        super(process, variableScope);
        this.process = process;
    }

    public void addChildElement(PropertyWriter p) {
        this.childElements.put(p.getElement().getId(), p);
        process.getFlowElements().add(p.getFlowElement());

        ElementParameters simulationParameters = p.getSimulationParameters();
        if (simulationParameters != null) {
            this.simulationParameters.add(simulationParameters);
        }

        this.itemDefinitions.addAll(p.itemDefinitions);
        this.dataInputs.addAll(p.dataInputs);
        this.dataOutputs.addAll(p.dataOutputs);
        this.rootElements.addAll(p.rootElements);
    }

    public BasePropertyWriter getChildElement(String id) {
        return this.childElements.get(id);
    }

    @Override
    public void addChildEdge(BPMNEdge edge) {

    }

    public void setDocumentation(String documentation) {
        Documentation d = bpmn2.createDocumentation();
        d.setText(asCData(documentation));
        process.getDocumentation().add(d);
    }

    public void setDescription(String value) {
        CustomElement.description.of(flowElement).set(value);
    }

    public void setSimulationSet(SimulationSet simulations) {
        ElementParameters elementParameters = SimulationSets.toElementParameters(simulations);
        elementParameters.setElementRef(this.baseElement.getId());
        this.simulationParameters.add(elementParameters);
    }

    // eww
    protected String asCData(String original) {
        return "<![CDATA[" + original + "]]>";
    }

    public void setProcessVariables(ProcessVariables processVariables) {
        String value = processVariables.getValue();
        DeclarationList declarationList = DeclarationList.fromString(value);

        List<Property> properties = process.getProperties();
        declarationList.getDeclarations().forEach(decl -> {
            VariableScope.Variable variable =
                    variableScope.declare(this.process.getId(), decl.getIdentifier(), decl.getType());
            properties.add(variable.getTypedIdentifier());
        });
    }

    public void addLaneSet(List<LanePropertyWriter> lanes) {
        if (lanes.isEmpty()) {
            return;
        }
        LaneSet laneSet = bpmn2.createLaneSet();
        List<org.eclipse.bpmn2.Lane> laneList = laneSet.getLanes();
        lanes.forEach(l -> laneList.add(l.getElement()));
        process.getLaneSets().add(laneSet);
        lanes.forEach(l -> {
            this.childElements.put(l.getElement().getId(), l);
        });
    }
}
