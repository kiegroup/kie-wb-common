package org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties;

import java.util.Collection;

import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.Process;
import org.eclipse.bpmn2.Relationship;
import org.eclipse.bpmn2.RootElement;
import org.eclipse.bpmn2.di.BPMNDiagram;

public class DefinitionsPropertyWriter {

    private final Definitions definitions;

    public DefinitionsPropertyWriter(Definitions definitions) {
        this.definitions = definitions;
    }

    public void setProcess(Process process) {
        definitions.getRootElements().add(process);
    }

    public void setDiagram(BPMNDiagram bpmnDiagram) {
        definitions.getDiagrams().add(bpmnDiagram);
    }

    public void setRelationship(Relationship relationship) {
        relationship.getSources().add(definitions);
        relationship.getTargets().add(definitions);
        definitions.getRelationships().add(relationship);
    }

    public void addAllRootElements(Collection<? extends RootElement> rootElements) {
        definitions.getRootElements().addAll(rootElements);
    }

    public Definitions getDefinitions() {
        return definitions;
    }
}
