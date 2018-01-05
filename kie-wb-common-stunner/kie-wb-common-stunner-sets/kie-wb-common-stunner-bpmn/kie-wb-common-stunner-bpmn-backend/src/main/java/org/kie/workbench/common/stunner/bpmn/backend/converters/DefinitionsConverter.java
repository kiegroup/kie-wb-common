package org.kie.workbench.common.stunner.bpmn.backend.converters;

import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.Process;
import org.eclipse.bpmn2.RootElement;
import org.kie.workbench.common.stunner.bpmn.backend.legacy.profile.IDiagramProfile;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDiagram;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDiagramImpl;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefinitionsConverter {
    private static final Logger _logger = LoggerFactory.getLogger(DefinitionsConverter.class);
    private final ProcessConverter processConverter;

    private TypedFactoryManager factoryManager;
    private final IDiagramProfile profile;

    public DefinitionsConverter(
            FactoryManager factoryManager,
            IDiagramProfile profile) {
        this.factoryManager = new TypedFactoryManager(factoryManager);
        this.profile = profile;

        this.processConverter = new ProcessConverter(this.factoryManager);
    }

    public Element<View<BPMNDiagramImpl>> convertDiagram(Definitions def) {
        Match<RootElement, Node<View<BPMNDiagramImpl>, ?>> matcher =
                Match.ofNode(RootElement.class, BPMNDiagramImpl.class)
                .when(Process.class, processConverter::convertDiagram);
        return def.getRootElements().stream()
                .map(matcher::apply)
                .findFirst()
                .get().get();
    }



}
