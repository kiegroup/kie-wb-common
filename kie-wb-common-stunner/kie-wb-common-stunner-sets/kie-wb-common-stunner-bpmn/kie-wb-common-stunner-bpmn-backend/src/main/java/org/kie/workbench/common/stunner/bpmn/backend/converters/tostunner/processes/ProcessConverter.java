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

package org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.processes;

import org.eclipse.bpmn2.SubProcess;
import org.kie.workbench.common.stunner.bpmn.backend.converters.TypedFactoryManager;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.BpmnNode;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.DefinitionResolver;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties.PropertyReaderFactory;

/**
 * Creates converters for Processes and SubProcesses
 * <p>
 * Processes and SubProcesses are alike, but are not exactly compatible
 * type-wise. However, they may contain the same type of nodes.
 * ProcessConverterFactory returns instances of ProcessConverters
 * and SubprocessConverters.
 */
public class ProcessConverter {

    private final TypedFactoryManager factoryManager;
    private final PropertyReaderFactory propertyReaderFactory;
    private final DefinitionResolver definitionResolver;
    private final ProcessConverterDelegate processConverterDelegate;

    public ProcessConverter(
            TypedFactoryManager typedFactoryManager,
            DefinitionResolver definitionResolver) {

        this.factoryManager = typedFactoryManager;
        this.definitionResolver = definitionResolver;
        this.propertyReaderFactory =
                new PropertyReaderFactory(definitionResolver);
        this.processConverterDelegate =
                new ProcessConverterDelegate(factoryManager, definitionResolver, this);
    }

    public BpmnNode convertSubProcess(SubProcess subProcess) {
        SubProcessConverter subProcessConverter = new SubProcessConverter(
                factoryManager,
                propertyReaderFactory,
                processConverterDelegate);
        return subProcessConverter.convertSubProcess(subProcess);
    }

    public BpmnNode convertProcess() {
        RootProcessConverter rootProcessConverter = new RootProcessConverter(
                factoryManager,
                propertyReaderFactory,
                definitionResolver,
                processConverterDelegate);
        return rootProcessConverter.convertProcess();
    }
}
