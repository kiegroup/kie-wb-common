/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.dmn.client.shape;

import org.kie.workbench.common.dmn.api.definition.v1_1.KnowledgeRequirement;
import org.kie.workbench.common.dmn.client.shape.view.KnowledgeRequirementView;
import org.kie.workbench.common.stunner.shapes.client.BasicConnectorShape;
import org.kie.workbench.common.stunner.shapes.def.ConnectorShapeDef;

public class KnowledgeRequirementShape extends BasicConnectorShape<KnowledgeRequirement, ConnectorShapeDef<KnowledgeRequirement>, KnowledgeRequirementView> {

    public KnowledgeRequirementShape(final ConnectorShapeDef<KnowledgeRequirement> shapeDef,
                                     final KnowledgeRequirementView view) {
        super(shapeDef,
              view);
    }
}
