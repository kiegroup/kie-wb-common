package org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import bpsim.BPSimDataType;
import bpsim.BpsimPackage;
import bpsim.ElementParameters;
import bpsim.Scenario;
import bpsim.ScenarioParameters;
import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.Documentation;
import org.eclipse.bpmn2.ExtensionAttributeValue;
import org.eclipse.bpmn2.FlowElement;
import org.eclipse.bpmn2.LaneSet;
import org.eclipse.bpmn2.Process;
import org.eclipse.bpmn2.Property;
import org.eclipse.bpmn2.Relationship;
import org.eclipse.bpmn2.RootElement;
import org.eclipse.bpmn2.di.BPMNDiagram;
import org.eclipse.bpmn2.di.BPMNEdge;
import org.eclipse.bpmn2.di.BPMNPlane;
import org.eclipse.bpmn2.di.BPMNShape;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.impl.EStructuralFeatureImpl;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.kie.workbench.common.stunner.bpmn.backend.converters.customproperties.CustomAttribute;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.ElementContainer;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.DeclarationList;
import org.kie.workbench.common.stunner.bpmn.definition.property.variables.ProcessVariables;

import static org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.Factories.bpmn2;
import static org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.Factories.bpsim;
import static org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.Factories.di;

public class ProcessPropertyWriter extends BasePropertyWriter implements ElementContainer {

    private final Process process;
    private final BPMNDiagram bpmnDiagram;
    private Map<String, BasePropertyWriter> childElements = new HashMap<>();
    private Collection<ElementParameters> simulationParameters = new ArrayList<>();

    public ProcessPropertyWriter(Process process, VariableScope variableScope) {
        super(process, variableScope);
        this.process = process;

        this.bpmnDiagram = di.createBPMNDiagram();
        bpmnDiagram.setId(process.getId());

        BPMNPlane bpmnPlane = di.createBPMNPlane();
        bpmnDiagram.setPlane(bpmnPlane);
    }

    public Process getProcess() {
        return process;
    }

    public void addChildShape(BPMNShape shape) {
        if (shape != null) {
            bpmnDiagram.getPlane().getPlaneElement().add(shape);
        }
    }

    public void addChildEdge(BPMNEdge edge) {
        bpmnDiagram.getPlane().getPlaneElement().add(edge);
    }

    public BPMNDiagram getBpmnDiagram() {
        return bpmnDiagram;
    }

    // fixme these instanceof have to go!
    public void addChildElement(BasePropertyWriter p) {
        this.childElements.put(p.getElement().getId(), p);
        if (p.getElement() instanceof FlowElement) {
            process.getFlowElements().add((FlowElement) p.getElement());
        }
        if (p instanceof CatchEventPropertyWriter) {
            ElementParameters simulationParameters = ((CatchEventPropertyWriter) p).getSimulationParameters();
            if (simulationParameters != null) {
                this.simulationParameters.add(simulationParameters);
            }
        }

        if (p instanceof ActivityPropertyWriter) {
            ElementParameters simulationParameters = ((ActivityPropertyWriter) p).getSimulationParameters();
            if (simulationParameters != null) {
                this.simulationParameters.add(simulationParameters);
            }
        }

        if (p instanceof SubProcessPropertyWriter) {
            Collection<ElementParameters> simulationParameters = ((SubProcessPropertyWriter) p).getSimulationParameters();
            this.simulationParameters.addAll(simulationParameters);
        }

        if (p instanceof SequenceFlowPropertyWriter) {
            addChildEdge(((SequenceFlowPropertyWriter) p).getEdge());
        }
        addChildShape(p.getShape());
        addAllBaseElements(p.getBaseElements());
    }

    public BasePropertyWriter getChildElement(String id) {
        return this.childElements.get(id);
    }

    protected void addBaseElement(BaseElement element) {
        if (element instanceof Property) {
            process.getProperties().add((Property) element);
        } else {
            super.addBaseElement(element);
        }
    }

    public void addAllBaseElements(Collection<BaseElement> baseElements) {
        baseElements.forEach(this::addBaseElement);
    }

    public void setName(String value) {
        process.setName(value);
    }

    public void setExecutable(Boolean value) {
        process.setIsExecutable(value);
    }

    public void setDocumentation(String documentation) {
        Documentation d = bpmn2.createDocumentation();
        d.setText(asCData(documentation));
        process.getDocumentation().add(d);
    }

    public void setPackage(String value) {
        CustomAttribute.packageName.of(process).set(value);
    }

    public void setVersion(String value) {
        CustomAttribute.version.of(process).set(value);
    }

    public void setAdHoc(Boolean adHoc) {
        process.getAnyAttribute().add(attribute("adHoc", String.valueOf(adHoc)));
    }

    public void setDescription(String value) {
        setMeta("customDescription", value);
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

    public Collection<ElementParameters> getSimulationParameters() {
        return simulationParameters;
    }

    private static final String defaultRelationshipType = "BPSimData";

    public Relationship getRelationship() {
        Relationship relationship = bpmn2.createRelationship();
        relationship.setType(defaultRelationshipType);
        BPSimDataType simDataType = bpsim.createBPSimDataType();
        // currently support single scenario
        Scenario defaultScenario = bpsim.createScenario();
        ScenarioParameters scenarioParameters = bpsim.createScenarioParameters();
        defaultScenario.setId("default"); // single scenario suppoert
        defaultScenario.setName("Simulationscenario"); // single scenario support
        defaultScenario.setScenarioParameters(scenarioParameters);
        simDataType.getScenario().add(defaultScenario);
        ExtensionAttributeValue extensionElement = bpmn2.createExtensionAttributeValue();
        relationship.getExtensionValues().add(extensionElement);
        FeatureMap.Entry extensionElementEntry = new EStructuralFeatureImpl.SimpleFeatureMapEntry(
                (EStructuralFeature.Internal) BpsimPackage.Literals.DOCUMENT_ROOT__BP_SIM_DATA,
                simDataType);
        relationship.getExtensionValues().get(0).getValue().add(extensionElementEntry);
        defaultScenario.getElementParameters().addAll(simulationParameters);

        return relationship;
    }

    public Collection<RootElement> getRootElements() {
        return baseElements.values().stream().filter(e -> e instanceof RootElement)
                .map(e -> (RootElement) e)
                .collect(Collectors.toList());
    }
}
