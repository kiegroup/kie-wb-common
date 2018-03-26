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

package org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties;

import org.eclipse.bpmn2.AdHocOrdering;
import org.eclipse.bpmn2.AdHocSubProcess;
import org.eclipse.bpmn2.FormalExpression;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties.Scripts;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.AdHocCompletionCondition;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.ScriptTypeValue;

import static org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.Factories.bpmn2;

public class AdHocSubProcessPropertyWriter extends SubProcessPropertyWriter {

    private final AdHocSubProcess process;

    public AdHocSubProcessPropertyWriter(AdHocSubProcess process, VariableScope variableScope) {
        super(process, variableScope);
        this.process = process;
    }

    public void setAdHocOrdering(org.kie.workbench.common.stunner.bpmn.definition.property.task.AdHocOrdering value) {
        process.setOrdering(AdHocOrdering.getByName(value.getValue()));
    }

    public void setAdHocCompletionCondition(AdHocCompletionCondition adHocCompletionCondition) {
        FormalExpression e = bpmn2.createFormalExpression();
        ScriptTypeValue s = adHocCompletionCondition.getValue();
        e.setLanguage(Scripts.scriptLanguageToUri(s.getLanguage()));
        e.setBody(asCData(s.getScript()));
        process.setCompletionCondition(e);
    }
}
