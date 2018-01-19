package org.kie.workbench.common.stunner.bpmn.backend.converters;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import bpsim.BPSimDataType;
import bpsim.BpsimPackage;
import bpsim.ElementParameters;
import bpsim.Scenario;
import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.RootElement;
import org.eclipse.bpmn2.Signal;
import org.eclipse.emf.ecore.util.FeatureMap;

public class DefinitionResolver {

    private final Map<String, Signal> signals = new HashMap<>();
    private final Map<String, ElementParameters> elementParameters = new HashMap<>();

    public DefinitionResolver(Definitions definitions) {
        for (RootElement el : definitions.getRootElements()) {
            if (el instanceof Signal) {
                signals.put(el.getId(), (Signal) el);
            }
        }
        FeatureMap value = definitions.getRelationships().get(0).getExtensionValues().get(0).getValue();
        List<BPSimDataType> bpsimExtensions = (List<BPSimDataType>)
                value.get(BpsimPackage.Literals.DOCUMENT_ROOT__BP_SIM_DATA, true);
        Scenario scenario = bpsimExtensions.get(0).getScenario().get(0);
        for (ElementParameters parameters : scenario.getElementParameters()) {
            elementParameters.put(parameters.getElementRef(), parameters);
        }
    }

    public Optional<Signal> resolveSignal(String id) {
        return Optional.ofNullable(signals.get(id));
    }

    public Optional<ElementParameters> resolveSimulationParameters(String id) {
        return Optional.ofNullable(elementParameters.get(id));
    }
}
