package org.kie.workbench.common.stunner.bpmn.backend.converters;

import java.util.Optional;

import org.eclipse.bpmn2.di.BPMNPlane;
import org.eclipse.bpmn2.di.BPMNShape;
import org.eclipse.dd.dc.Bounds;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNViewDefinition;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.BoundsImpl;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

public class Layout {

    public static void updateNode(
            org.eclipse.bpmn2.di.BPMNPlane plane,
            Node<? extends View<? extends BPMNViewDefinition>, ?> node) {
        getBPMNShapeForElement(node.getUUID(), plane).ifPresent(shape -> {
            Bounds bounds = shape.getBounds();
            BoundsImpl convertedBounds = BoundsImpl.build(
                    bounds.getX(), bounds.getY(),
                    bounds.getX() + bounds.getWidth(), bounds.getY() + bounds.getHeight());
            node.getContent().setBounds(convertedBounds);
        });
    }

    private static Optional<BPMNShape> getBPMNShapeForElement(String elementId,
                                                              BPMNPlane plane) {
        return plane.getPlaneElement().stream()
                .filter(dia -> dia instanceof BPMNShape)
                .map(shape -> (BPMNShape) shape)
                .filter(shape -> shape.getBpmnElement().getId().equals(elementId))
                .findFirst();
    }
}
