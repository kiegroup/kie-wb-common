package org.kie.workbench.common.stunner.bpmn.backend.converters.tasks;

import java.util.List;

import org.eclipse.bpmn2.Task;
import org.jboss.drools.DroolsPackage;
import org.jboss.drools.OnEntryScriptType;
import org.jboss.drools.OnExitScriptType;
import org.kie.workbench.common.stunner.bpmn.backend.converters.properties.ScriptLanguages;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.OnExitAction;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.ScriptableExecutionSet;

public class Scripts {
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
}
