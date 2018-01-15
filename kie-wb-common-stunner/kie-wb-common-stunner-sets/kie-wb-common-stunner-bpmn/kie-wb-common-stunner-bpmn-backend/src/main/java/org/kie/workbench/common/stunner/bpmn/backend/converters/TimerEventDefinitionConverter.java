package org.kie.workbench.common.stunner.bpmn.backend.converters;

import org.eclipse.bpmn2.Error;
import org.eclipse.bpmn2.ErrorEventDefinition;
import org.eclipse.bpmn2.FormalExpression;
import org.eclipse.bpmn2.TimerEventDefinition;
import org.kie.workbench.common.stunner.bpmn.definition.Executable;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.error.ErrorExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.error.ErrorRef;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.timer.TimerExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.timer.TimerSettings;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.timer.TimerSettingsValue;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

public class TimerEventDefinitionConverter {

    private final TypedFactoryManager factoryManager;

    public TimerEventDefinitionConverter(TypedFactoryManager factoryManager) {
        this.factoryManager = factoryManager;
    }

    public <T extends Executable<? extends TimerExecutionSet>> Node<View<T>, Edge> convert(TimerEventDefinition e, String nodeId, Class<T> type) {
        return convertNode(e, factoryManager.newNode(nodeId, type));
    }

    private <T extends Node<? extends View<? extends Executable<? extends TimerExecutionSet>>, Edge>> T convertNode(TimerEventDefinition e, T node) {
        Executable<? extends TimerExecutionSet> definition = node.getContent().getDefinition();
        TimerSettings timerSettings = convertTimerEventDefinition(e);
        definition.getExecutionSet().setTimerSettings(timerSettings);
        return node;
    }


    private TimerSettings convertTimerEventDefinition(TimerEventDefinition e) {
        TimerSettingsValue timerSettingsValue = new TimerSettings().getValue();
        FormalExpression timeCycle = (FormalExpression) e.getTimeCycle();
        timerSettingsValue.setTimeCycle(timeCycle.getMixed().getValue(0).toString());
        timerSettingsValue.setTimeCycleLanguage(timeCycle.getLanguage());

        FormalExpression timeDate = (FormalExpression) e.getTimeDate();
        timerSettingsValue.setTimeDate(timeDate.getMixed().getValue(0).toString());

        FormalExpression timeDateDuration = (FormalExpression) e.getTimeDuration();
        timerSettingsValue.setTimeDuration(timeDateDuration.getMixed().getValue(0).toString());
        return new TimerSettings(timerSettingsValue);
    }

    public static void convertMessageRef(Error e, ErrorRef ref) {
        ref.setValue(e.getErrorCode());
    }
}
