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

package org.kie.workbench.common.stunner.bpmn.backend.converters;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.BoundaryEvent;
import org.eclipse.bpmn2.di.BPMNEdge;
import org.eclipse.bpmn2.di.BPMNPlane;
import org.eclipse.bpmn2.di.BPMNShape;
import org.eclipse.dd.dc.Point;
import org.kie.workbench.common.stunner.bpmn.backend.converters.properties.AbstractPropertyReader;
import org.kie.workbench.common.stunner.bpmn.backend.converters.properties.Properties;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.view.BoundsImpl;
import org.kie.workbench.common.stunner.core.graph.content.view.Connection;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShapeLayout extends AbstractPropertyReader {

    private static final Logger logger = LoggerFactory.getLogger(ShapeLayout.class);
    private final BPMNShape shape;

    BPMNPlane plane;

    public ShapeLayout(BPMNPlane plane, BPMNShape shape, BaseElement element) {
        super(element);
        this.plane = plane;
        this.shape = shape;
    }

    public Bounds getBounds() {
        org.eclipse.dd.dc.Bounds bounds = shape.getBounds();

        double x, y;

        if (element instanceof BoundaryEvent) {
            // then we must check the overrides
            Point2D docker = Properties.docker((BoundaryEvent) element);
            x = docker.getX();
            y = docker.getY();
        } else {
            x = bounds.getX();
            y = bounds.getY();
        }

        return BoundsImpl.build(
                x, y,
                x + bounds.getWidth(),
                y + bounds.getHeight());
    }

}
