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

package org.kie.workbench.common.stunner.bpmn.backend.converters.properties;

import org.eclipse.bpmn2.BoundaryEvent;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;

public class BoundaryEventPropertyReader extends AbstractPropertyReader {

    private final BoundaryEvent event;

    public BoundaryEventPropertyReader(BoundaryEvent element) {
        super(element);
        this.event = element;
    }

    public Point2D docker(BoundaryEvent e) {
        String dockerInfoStr = attribute("dockerinfo");

        dockerInfoStr = dockerInfoStr.substring(0, dockerInfoStr.length() - 1);
        String[] dockerInfoParts = dockerInfoStr.split("\\|");
        String infoPartsToUse = dockerInfoParts[0];
        String[] infoPartsToUseParts = infoPartsToUse.split("\\^");

        double x = Double.valueOf(infoPartsToUseParts[0]);
        double y = Double.valueOf(infoPartsToUseParts[1]);

        return Point2D.create(x, y);
    }


}
