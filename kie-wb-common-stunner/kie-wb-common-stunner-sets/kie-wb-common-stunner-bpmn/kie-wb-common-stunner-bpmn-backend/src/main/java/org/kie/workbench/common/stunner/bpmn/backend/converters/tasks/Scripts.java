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

package org.kie.workbench.common.stunner.bpmn.backend.converters.tasks;

import java.util.List;

import org.eclipse.bpmn2.ExtensionAttributeValue;
import org.eclipse.bpmn2.ScriptTask;
import org.eclipse.bpmn2.Task;
import org.jboss.drools.DroolsPackage;
import org.jboss.drools.OnEntryScriptType;
import org.jboss.drools.OnExitScriptType;
import org.kie.workbench.common.stunner.bpmn.backend.converters.properties.ScriptLanguages;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.OnEntryAction;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.OnExitAction;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.RuleFlowGroup;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.ScriptLanguage;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.ScriptTaskExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.ScriptableExecutionSet;

import static org.kie.workbench.common.stunner.bpmn.backend.converters.properties.Properties.findAnyAttribute;

public class Scripts {

    @Deprecated
    public static void setProperties(Task task, ScriptableExecutionSet executionSet) {
        @SuppressWarnings("unchecked")
        List<OnEntryScriptType> onEntryExtensions =
                (List<OnEntryScriptType>) task.getExtensionValues().get(0).getValue()
                        .get(DroolsPackage.Literals.DOCUMENT_ROOT__ON_ENTRY_SCRIPT, true);
        @SuppressWarnings("unchecked")
        List<OnExitScriptType> onExitExtensions =
                (List<OnExitScriptType>) task.getExtensionValues().get(0).getValue()
                        .get(DroolsPackage.Literals.DOCUMENT_ROOT__ON_EXIT_SCRIPT, true);

        if (!onEntryExtensions.isEmpty()) {
            executionSet.getOnEntryAction().setValue(onEntryExtensions.get(0).getScript());
            executionSet.getScriptLanguage().setValue(ScriptLanguages.fromUri(onEntryExtensions.get(0).getScriptFormat()));
        }

        if (!onExitExtensions.isEmpty()) {
            executionSet.setOnExitAction(new OnExitAction(onExitExtensions.get(0).getScript()));
        }
    }

    public static RuleFlowGroup ruleFlowGroup(org.eclipse.bpmn2.BusinessRuleTask task) {
        return new RuleFlowGroup(findAnyAttribute(task, "ruleFlowGroup"));
    }

    public static OnEntryAction onEntry(Task task) {
        return new OnEntryAction(onEntry(task.getExtensionValues()));
    }

    public static String onEntry(List<ExtensionAttributeValue> extensions) {
        @SuppressWarnings("unchecked")
        List<OnEntryScriptType> onEntryExtensions =
                (List<OnEntryScriptType>) extensions.get(0).getValue()
                        .get(DroolsPackage.Literals.DOCUMENT_ROOT__ON_ENTRY_SCRIPT, true);

        if (!onEntryExtensions.isEmpty()) {
            return (onEntryExtensions.get(0).getScript());
        }

        return "";
    }

    public static ScriptLanguage scriptLanguage(Task task) {
        return new ScriptLanguage(scriptLanguage(task.getExtensionValues()));
    }

    public static String scriptLanguage(List<ExtensionAttributeValue> extensions) {
        @SuppressWarnings("unchecked")
        List<OnEntryScriptType> onEntryExtensions =
                (List<OnEntryScriptType>) extensions.get(0).getValue()
                        .get(DroolsPackage.Literals.DOCUMENT_ROOT__ON_ENTRY_SCRIPT, true);

        if (!onEntryExtensions.isEmpty()) {
            return (ScriptLanguages.fromUri(onEntryExtensions.get(0).getScriptFormat()));
        }
        return "";
    }

    public static OnExitAction onExit(Task task) {
        return new OnExitAction(onExit(task.getExtensionValues()));
    }

    public static String onExit(List<ExtensionAttributeValue> extensions) {
        @SuppressWarnings("unchecked")
        List<OnExitScriptType> onExitExtensions =
                (List<OnExitScriptType>) extensions.get(0).getValue()
                        .get(DroolsPackage.Literals.DOCUMENT_ROOT__ON_EXIT_SCRIPT, true);

        if (!onExitExtensions.isEmpty()) {
            return (onExitExtensions.get(0).getScript());
        }

        return "";
    }

    public static void setProperties(ScriptTask task, ScriptTaskExecutionSet executionSet) {
        executionSet.getScript().setValue(task.getScript());
        executionSet.getScriptLanguage().setValue(ScriptLanguages.fromUri(task.getScriptFormat()));
    }
}
