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

import org.eclipse.bpmn2.Definitions;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.BpmnProcessNode;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.DefinitionResolver;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.processes.ProcessConverterFactory;

public class DiagramConverter {

    private final ProcessConverterFactory processConverterFactory;
    private final DefinitionResolver definitionResolver;

    public DiagramConverter(ProcessConverterFactory processConverterFactory, DefinitionResolver definitionResolver) {
        this.processConverterFactory = processConverterFactory;
        this.definitionResolver = definitionResolver;
    }

    public DiagramConverter(TypedFactoryManager typedFactoryManager, Definitions definitions) {
        // the stunner model aggregates in a node different aspects:
        // - type (e.g., Task)
        // - format (e.g., colors)
        // - layout (position)
        // thus, we need a mechanism to resolve these different concerns
        // as we convert FlowElements
        this.definitionResolver =
                new DefinitionResolver(definitions);

        // process converters are a bit more involved than other
        // converters, so we use a factory
        this.processConverterFactory =
                new ProcessConverterFactory(
                        typedFactoryManager,
                        definitionResolver);
    }

    public BpmnProcessNode getDiagramRoot() {
        return processConverterFactory
                .processConverter()
                .convertProcess();
    }
}
