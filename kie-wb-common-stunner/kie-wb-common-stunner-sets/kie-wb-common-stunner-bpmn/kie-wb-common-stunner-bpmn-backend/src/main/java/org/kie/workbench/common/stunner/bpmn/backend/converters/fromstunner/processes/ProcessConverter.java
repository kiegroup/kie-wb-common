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

package org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.processes;

import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.DefinitionsBuildingContext;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties.ProcessPropertyWriter;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties.PropertyWriter;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties.PropertyWriterFactory;
import org.kie.workbench.common.stunner.bpmn.definition.BaseSubprocess;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

public class ProcessConverter {

    private final DefinitionsBuildingContext context;
    private final PropertyWriterFactory propertyWriterFactory;
    private final ProcessConverterDelegate processConverterDelegate;

    public ProcessConverter(DefinitionsBuildingContext context, PropertyWriterFactory propertyWriterFactory) {
        this.context = context;
        this.propertyWriterFactory = propertyWriterFactory;
        this.processConverterDelegate = new ProcessConverterDelegate(propertyWriterFactory, this);
    }

    public PropertyWriter convertSubProcess(Node<View<BaseSubprocess>, ?> node) {
        SubProcessConverter subProcessConverter = new SubProcessConverter(
                context,
                propertyWriterFactory,
                processConverterDelegate);
        return subProcessConverter.convertSubProcess(node);
    }

    public ProcessPropertyWriter convertProcess() {
        RootProcessConverter rootProcessConverter = new RootProcessConverter(
                context,
                propertyWriterFactory,
                processConverterDelegate);
        return rootProcessConverter.convertProcess();
    }
}