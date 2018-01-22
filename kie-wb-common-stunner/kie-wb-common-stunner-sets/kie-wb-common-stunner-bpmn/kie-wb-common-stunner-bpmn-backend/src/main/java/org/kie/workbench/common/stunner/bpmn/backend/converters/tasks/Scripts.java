package org.kie.workbench.common.stunner.bpmn.backend.converters.tasks;

import java.util.List;

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
        @SuppressWarnings("unchecked")
        List<OnEntryScriptType> onEntryExtensions =
                (List<OnEntryScriptType>) task.getExtensionValues().get(0).getValue()
                        .get(DroolsPackage.Literals.DOCUMENT_ROOT__ON_ENTRY_SCRIPT, true);

        if (!onEntryExtensions.isEmpty()) {
            return new OnEntryAction(onEntryExtensions.get(0).getScript());
        }

        return new OnEntryAction();
    }


    public static ScriptLanguage scriptLanguage(Task task) {
        @SuppressWarnings("unchecked")
        List<OnEntryScriptType> onEntryExtensions =
                (List<OnEntryScriptType>) task.getExtensionValues().get(0).getValue()
                        .get(DroolsPackage.Literals.DOCUMENT_ROOT__ON_ENTRY_SCRIPT, true);

        if (!onEntryExtensions.isEmpty()) {
            return new ScriptLanguage(ScriptLanguages.fromUri(onEntryExtensions.get(0).getScriptFormat()));
        }
        return new ScriptLanguage();
    }



    public static OnExitAction onExit(Task task) {
        @SuppressWarnings("unchecked")
        List<OnExitScriptType> onExitExtensions =
                (List<OnExitScriptType>) task.getExtensionValues().get(0).getValue()
                        .get(DroolsPackage.Literals.DOCUMENT_ROOT__ON_EXIT_SCRIPT, true);

        if (!onExitExtensions.isEmpty()) {
            return new OnExitAction(onExitExtensions.get(0).getScript());
        }

        return new OnExitAction();
    }


    public static void setProperties(ScriptTask task, ScriptTaskExecutionSet executionSet) {
        executionSet.getScript().setValue(task.getScript());
        executionSet.getScriptLanguage().setValue(ScriptLanguages.fromUri(task.getScriptFormat()));
    }
}
