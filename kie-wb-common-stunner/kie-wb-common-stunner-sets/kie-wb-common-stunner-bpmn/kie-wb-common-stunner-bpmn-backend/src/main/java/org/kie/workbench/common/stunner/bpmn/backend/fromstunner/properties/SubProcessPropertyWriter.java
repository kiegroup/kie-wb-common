package org.kie.workbench.common.stunner.bpmn.backend.fromstunner.properties;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bpsim.ElementParameters;
import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.Documentation;
import org.eclipse.bpmn2.FlowElement;
import org.eclipse.bpmn2.LaneSet;
import org.eclipse.bpmn2.Property;
import org.eclipse.bpmn2.SubProcess;
import org.eclipse.bpmn2.di.BPMNEdge;
import org.kie.workbench.common.stunner.bpmn.backend.converters.properties.Attribute;
import org.kie.workbench.common.stunner.bpmn.backend.converters.properties.CustomElement;
import org.kie.workbench.common.stunner.bpmn.backend.converters.properties.SimulationSets;
import org.kie.workbench.common.stunner.bpmn.backend.fromstunner.ElementContainer;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.DeclarationList;
import org.kie.workbench.common.stunner.bpmn.definition.property.simulation.SimulationSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.variables.ProcessVariables;

import static org.kie.workbench.common.stunner.bpmn.backend.fromstunner.Factories.bpmn2;

public class SubProcessPropertyWriter extends PropertyWriter implements ElementContainer {

    public static final String defaultRelationshipType = "BPSimData";

    private final SubProcess process;
    private Collection<ElementParameters> simulationParameters = new ArrayList<>();
    private Map<String, BasePropertyWriter> childElements = new HashMap<>();

    public SubProcessPropertyWriter(SubProcess process, VariableScope variableScope) {
        super(process, variableScope);
        this.process = process;
    }

    public void addSimulationSet(SimulationSet simulations) {
        this.simulationParameters.add(SimulationSets.toElementParameters(simulations));
    }

    public Collection<ElementParameters> getSimulationParameters() {
        return simulationParameters;
    }

    public void addChildElement(BasePropertyWriter p) {
        this.childElements.put(p.getElement().getId(), p);
        if (p.getElement() instanceof FlowElement) {
            process.getFlowElements().add((FlowElement) p.getElement());
        }
        if (p instanceof ActivityPropertyWriter) {
            ElementParameters simulationParameters = ((ActivityPropertyWriter) p).getSimulationParameters();
            if (simulationParameters != null) {
                this.simulationParameters.add(simulationParameters);
            }
        }

        addAllBaseElements(p.getBaseElements());
    }

    public BasePropertyWriter getChildElement(String id) {
        return this.childElements.get(id);
    }

    @Override
    public void addChildEdge(BPMNEdge edge) {

    }

    public void addAllBaseElements(Collection<BaseElement> baseElements) {
        baseElements.forEach(el -> this.baseElements.put(el.getId(), el));
    }

//    public void setName(String value) {
//        process.setName(value);
//    }

    public void setDocumentation(String documentation) {
        Documentation d = bpmn2.createDocumentation();
        d.setText(asCData(documentation));
        process.getDocumentation().add(d);
    }

    public void setPackage(String value) {
        Attribute.packageName.of(flowElement).set(value);
    }

    public void setVersion(String value) {
        Attribute.version.of(flowElement).set(value);
    }

    public void setAdHoc(Boolean adHoc) {
        Attribute.adHoc.of(flowElement).set(adHoc);
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
            addBaseElement(variable.getTypeDeclaration());
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
    }
}
