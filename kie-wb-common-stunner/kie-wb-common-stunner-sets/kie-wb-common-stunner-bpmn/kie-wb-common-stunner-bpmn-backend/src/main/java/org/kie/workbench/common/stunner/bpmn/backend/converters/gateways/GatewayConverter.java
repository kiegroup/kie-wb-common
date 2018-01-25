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

package org.kie.workbench.common.stunner.bpmn.backend.converters.gateways;

import org.kie.workbench.common.stunner.bpmn.backend.converters.Match;
import org.kie.workbench.common.stunner.bpmn.backend.converters.TypedFactoryManager;
import org.kie.workbench.common.stunner.bpmn.backend.converters.properties.Properties;
import org.kie.workbench.common.stunner.bpmn.definition.BaseGateway;
import org.kie.workbench.common.stunner.bpmn.definition.ExclusiveDatabasedGateway;
import org.kie.workbench.common.stunner.bpmn.definition.ParallelGateway;
import org.kie.workbench.common.stunner.bpmn.definition.property.gateway.DefaultRoute;
import org.kie.workbench.common.stunner.bpmn.definition.property.gateway.ExclusiveGatewayExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.BPMNGeneralSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Name;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

public class GatewayConverter {

    private final TypedFactoryManager factoryManager;

    public GatewayConverter(TypedFactoryManager factoryManager) {
        this.factoryManager = factoryManager;
    }

    public Node<? extends View<? extends BaseGateway>, ?> convert(org.eclipse.bpmn2.Gateway gateway) {
        return Match.ofNode(org.eclipse.bpmn2.Gateway.class, BaseGateway.class)
                .when(org.eclipse.bpmn2.ParallelGateway.class, e -> factoryManager.newNode(gateway.getId(), ParallelGateway.class))
                .when(org.eclipse.bpmn2.ExclusiveGateway.class, e -> {
                    Node<View<ExclusiveDatabasedGateway>, Edge> node = factoryManager.newNode(gateway.getId(), ExclusiveDatabasedGateway.class);

                    ExclusiveDatabasedGateway definition = node.getContent().getDefinition();

                    definition.setGeneral(new BPMNGeneralSet(
                            new Name(gateway.getName()),
                            Properties.documentation(gateway.getDocumentation())
                    ));

                    definition.setExecutionSet(new ExclusiveGatewayExecutionSet(
                            new DefaultRoute(Properties.findAnyAttribute(gateway, "dg"))
                    ));

                    return node;
                })
                .apply(gateway).asSuccess().value();
    }
}
