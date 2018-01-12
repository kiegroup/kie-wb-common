package org.kie.workbench.common.stunner.bpmn.backend.converters;

import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

public class Cast {
    // workaround for variance issues,
    // e.g., we can't just convert Node<View<BaseStartEvent>, ?> to Node<View<BPMNViewDefinition>, ?>
    public static <T> Node<View<T>, ?> to(Element<?> convertedStartEvent) {
        return (Node<View<T>, ?>) convertedStartEvent;
    }
}
