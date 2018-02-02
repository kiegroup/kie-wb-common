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

import org.eclipse.bpmn2.Activity;
import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.BusinessRuleTask;
import org.eclipse.bpmn2.Event;
import org.eclipse.bpmn2.Gateway;
import org.eclipse.bpmn2.Process;
import org.eclipse.bpmn2.ScriptTask;
import org.eclipse.bpmn2.SequenceFlow;
import org.eclipse.bpmn2.SubProcess;
import org.eclipse.bpmn2.UserTask;
import org.eclipse.bpmn2.di.BPMNPlane;
import org.kie.workbench.common.stunner.bpmn.backend.converters.DefinitionResolver;

public class PropertyReaderFactory {

    private final BPMNPlane plane;
    private final DefinitionResolver definitionResolver;

    public PropertyReaderFactory(BPMNPlane plane, DefinitionResolver definitionResolver) {
        this.plane = plane;
        this.definitionResolver = definitionResolver;
    }

    public BasicPropertyReader of(BaseElement el) {
        return new BasicPropertyReader(el, plane);
    }

    public SequenceFlowPropertyReader of(SequenceFlow el) {
        return new SequenceFlowPropertyReader(el, plane);
    }

    public GatewayPropertyReader of(Gateway el) {
        return new GatewayPropertyReader(el, plane);
    }

    public UserTaskPropertyReader of(UserTask el) {
        return new UserTaskPropertyReader(el, plane, definitionResolver);
    }

    public EventPropertyReader of(Event el) {
        return EventPropertyReader.of(el, plane, definitionResolver);
    }

    public SubProcessPropertyReader of(SubProcess el) {
        return new SubProcessPropertyReader(el, plane);
    }

    public BusinessRuleTaskPropertyReader of(BusinessRuleTask el) {
        return new BusinessRuleTaskPropertyReader(el, plane);
    }

    public ActivityPropertyReader of(Activity el) {
        return new ActivityPropertyReader(el, plane);
    }

    public ProcessPropertyReader of(Process el) {
        return new ProcessPropertyReader(el, plane);
    }

    public ScriptTaskPropertyReader of(ScriptTask el) {
        return new ScriptTaskPropertyReader(el, plane);
    }
}
