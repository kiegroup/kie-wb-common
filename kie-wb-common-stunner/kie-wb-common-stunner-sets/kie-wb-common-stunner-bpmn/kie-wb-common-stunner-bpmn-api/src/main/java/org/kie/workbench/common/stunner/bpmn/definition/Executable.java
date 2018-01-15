package org.kie.workbench.common.stunner.bpmn.definition;

public interface Executable<T> {
    T getExecutionSet();
    void setExecutionSet(T executionSet);
}
