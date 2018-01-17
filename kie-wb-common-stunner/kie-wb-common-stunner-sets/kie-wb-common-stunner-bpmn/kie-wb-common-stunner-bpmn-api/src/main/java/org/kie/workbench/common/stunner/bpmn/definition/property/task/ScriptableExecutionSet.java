package org.kie.workbench.common.stunner.bpmn.definition.property.task;

public interface ScriptableExecutionSet {

    OnEntryAction getOnEntryAction();

    void setOnEntryAction(OnEntryAction onEntryAction);

    OnExitAction getOnExitAction();

    void setOnExitAction(OnExitAction onExitAction);

    ScriptLanguage getScriptLanguage();

    void setScriptLanguage(ScriptLanguage scriptLanguage);
}
