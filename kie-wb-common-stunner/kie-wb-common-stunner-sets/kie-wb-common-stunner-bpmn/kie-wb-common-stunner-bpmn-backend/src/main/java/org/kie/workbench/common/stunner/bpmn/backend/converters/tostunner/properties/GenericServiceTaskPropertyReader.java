/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties;

import java.util.Optional;

import org.eclipse.bpmn2.Operation;
import org.eclipse.bpmn2.ServiceTask;
import org.eclipse.bpmn2.di.BPMNDiagram;
import org.kie.workbench.common.stunner.bpmn.backend.converters.customproperties.CustomAttribute;
import org.kie.workbench.common.stunner.bpmn.backend.converters.customproperties.CustomElement;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.DefinitionResolver;
import org.kie.workbench.common.stunner.bpmn.definition.property.service.GenericServiceTaskValue;
import org.kie.workbench.common.stunner.core.util.StringUtils;

public class GenericServiceTaskPropertyReader extends MultipleInstanceActivityPropertyReader {

    public static final String JAVA = "Java";
    public static final String WEB_SERVICE = "WebService";
    private final ServiceTask task;

    public GenericServiceTaskPropertyReader(ServiceTask task, BPMNDiagram diagram, DefinitionResolver definitionResolver) {
        super(task, diagram, definitionResolver);
        this.task = task;
    }

    public GenericServiceTaskValue getGenericServiceTask() {
        GenericServiceTaskValue value = new GenericServiceTaskValue();
        final String implementation = Optional.ofNullable(CustomAttribute.serviceImplementation.of(task).get())
                .filter(StringUtils::nonEmpty)
                .orElseGet(() -> task.getImplementation());
        value.setServiceImplementation(Optional.ofNullable(implementation)
                                               .filter(impl -> JAVA.equalsIgnoreCase(impl))
                                               .orElse(WEB_SERVICE));

        final String operation = Optional.ofNullable(CustomAttribute.serviceOperation.of(task).get())
                .filter(StringUtils::nonEmpty)
                .orElseGet(() -> Optional.ofNullable(task.getOperationRef()).map(Operation::getName).orElse(null));
        value.setServiceOperation(operation);

        final String serviceInterface = CustomAttribute.serviceInterface.of(task).get();
        value.setServiceInterface(serviceInterface);

        return value;
    }


    public boolean isAsync() {
        return CustomElement.async.of(element).get();
    }

    public boolean isAdHocAutostart() {
        return CustomElement.autoStart.of(element).get();
    }

    public String getSLADueDate() {
        return CustomElement.slaDueDate.of(element).get();
    }
}
