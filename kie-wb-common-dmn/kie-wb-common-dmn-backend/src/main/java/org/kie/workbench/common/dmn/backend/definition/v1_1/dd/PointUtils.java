/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.backend.definition.v1_1.dd;

import java.util.List;

import org.kie.dmn.model.api.dmndi.DMNShape;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Child;
import org.kie.workbench.common.stunner.core.graph.content.view.BoundImpl;
import org.kie.workbench.common.stunner.core.graph.content.view.BoundsImpl;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

public class PointUtils {

    private PointUtils() {
        // util class.
    }

    public static org.kie.dmn.model.api.dmndi.Point point2dToDMNDIPoint(org.kie.workbench.common.stunner.core.graph.content.view.Point2D point2d) {
        org.kie.dmn.model.api.dmndi.Point result = new org.kie.dmn.model.v1_2.dmndi.Point();
        result.setX(point2d.getX());
        result.setY(point2d.getY());
        return result;
    }

    public static org.kie.workbench.common.stunner.core.graph.content.view.Point2D dmndiPointToPoint2D(org.kie.dmn.model.api.dmndi.Point dmndiPoint) {
        org.kie.workbench.common.stunner.core.graph.content.view.Point2D result = new org.kie.workbench.common.stunner.core.graph.content.view.Point2D(dmndiPoint.getX(), dmndiPoint.getY());
        return result;
    }

    // In Stunner terms the location of a child (target) is always relative to the
    // Parent (source) location however DMN requires all locations to be absolute.
    @SuppressWarnings("unchecked")
    public static void convertToAbsoluteBounds(final Node<?, ?> targetNode) {
        if (targetNode.getContent() instanceof View<?>) {
            final View<?> targetNodeView = (View<?>) targetNode.getContent();
            double boundsX = xOfBound(upperLeftBound(targetNodeView));
            double boundsY = yOfBound(upperLeftBound(targetNodeView));
            final double boundsWidth = xOfBound(lowerRightBound(targetNodeView)) - boundsX;
            final double boundsHeight = yOfBound(lowerRightBound(targetNodeView)) - boundsY;
            final List<Edge<?, ?>> inEdges = (List<Edge<?, ?>>) targetNode.getInEdges();
            for (Edge<?, ?> e : inEdges) {
                if (e.getContent() instanceof Child) {
                    final Node<?, ?> sourceNode = e.getSourceNode();
                    final View<?> sourceView = (View<?>) sourceNode.getContent();
                    final org.kie.workbench.common.stunner.core.graph.content.Bounds.Bound sourceViewULBound = sourceView.getBounds().getUpperLeft();
                    final double dx = sourceViewULBound.getX();
                    final double dy = sourceViewULBound.getY();
                    boundsX = boundsX + dx;
                    boundsY = boundsY + dy;
                    targetNodeView.setBounds(new BoundsImpl(new BoundImpl(boundsX, boundsY),
                                                            new BoundImpl(boundsX + boundsWidth, boundsY + boundsHeight)));
                    break;
                }
            }
        }
    }

    // In DMN terms the location of a Node is always absolute however Stunner requires
    // children (target) to have a relative location to their Parent (source).
    @SuppressWarnings("unchecked")
    public static void convertToRelativeBounds(final Node<?, ?> targetNode) {
        if (targetNode.getContent() instanceof View<?>) {
            final View<?> targetNodeView = (View<?>) targetNode.getContent();
            double boundsX = xOfBound(upperLeftBound(targetNodeView));
            double boundsY = yOfBound(upperLeftBound(targetNodeView));
            final double boundsWidth = xOfBound(lowerRightBound(targetNodeView)) - boundsX;
            final double boundsHeight = yOfBound(lowerRightBound(targetNodeView)) - boundsY;
            final List<Edge<?, ?>> inEdges = (List<Edge<?, ?>>) targetNode.getInEdges();
            for (Edge<?, ?> e : inEdges) {
                if (e.getContent() instanceof Child) {
                    final Node<?, ?> sourceNode = e.getSourceNode();
                    final View<?> sourceView = (View<?>) sourceNode.getContent();
                    final org.kie.workbench.common.stunner.core.graph.content.Bounds.Bound sourceViewULBound = sourceView.getBounds().getUpperLeft();
                    final double dx = sourceViewULBound.getX();
                    final double dy = sourceViewULBound.getY();
                    boundsX = boundsX - dx;
                    boundsY = boundsY - dy;
                    targetNodeView.setBounds(new BoundsImpl(new BoundImpl(boundsX, boundsY),
                                                            new BoundImpl(boundsX + boundsWidth, boundsY + boundsHeight)));
                    break;
                }
            }
        }
    }

    public static double xOfShape(final DMNShape shape) {
        if (shape != null) {
            if (shape.getBounds() != null) {
                return shape.getBounds().getX();
            }
        }
        return 0.0;
    }

    public static double yOfShape(final DMNShape shape) {
        if (shape != null) {
            if (shape.getBounds() != null) {
                return shape.getBounds().getY();
            }
        }
        return 0.0;
    }

    public static double widthOfShape(final DMNShape shape) {
        if (shape != null) {
            if (shape.getBounds() != null) {
                return shape.getBounds().getWidth();
            }
        }
        return 0.0;
    }

    public static double heightOfShape(final DMNShape shape) {
        if (shape != null) {
            if (shape.getBounds() != null) {
                return shape.getBounds().getHeight();
            }
        }
        return 0.0;
    }

    public static org.kie.workbench.common.stunner.core.graph.content.Bounds.Bound upperLeftBound(final View view) {
        if (view != null) {
            if (view.getBounds() != null) {
                return view.getBounds().getUpperLeft();
            }
        }
        return null;
    }

    public static org.kie.workbench.common.stunner.core.graph.content.Bounds.Bound lowerRightBound(final View view) {
        if (view != null) {
            if (view.getBounds() != null) {
                return view.getBounds().getLowerRight();
            }
        }
        return null;
    }

    public static double xOfBound(final org.kie.workbench.common.stunner.core.graph.content.Bounds.Bound bound) {
        if (bound != null) {
            return bound.getX();
        }
        return 0.0;
    }

    public static double yOfBound(final org.kie.workbench.common.stunner.core.graph.content.Bounds.Bound bound) {
        if (bound != null) {
            return bound.getY();
        }
        return 0.0;
    }
}
