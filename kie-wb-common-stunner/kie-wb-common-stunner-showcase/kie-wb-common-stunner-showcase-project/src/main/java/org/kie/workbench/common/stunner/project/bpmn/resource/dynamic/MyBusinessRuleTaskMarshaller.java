/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.project.bpmn.resource.dynamic;

import java.util.Map;

import javax.enterprise.context.Dependent;

import org.codehaus.jackson.JsonGenerator;
import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.FlowElement;
import org.kie.workbench.common.stunner.bpmn.backend.legacy.DynamicDefinitionMarshaller;

@Dependent
public class MyBusinessRuleTaskMarshaller implements DynamicDefinitionMarshaller {

    @Override
    public void applyProperties(BaseElement baseElement,
                                Map<String, String> properties) {

    }

    @Override
    public void marshallProperties(FlowElement element,
                                   Map<String, Object> properties,
                                   JsonGenerator generator) {
    }

    @Override
    public String getClassName() {
        return MyBusinessRuleTask.class.getName();
    }
}
