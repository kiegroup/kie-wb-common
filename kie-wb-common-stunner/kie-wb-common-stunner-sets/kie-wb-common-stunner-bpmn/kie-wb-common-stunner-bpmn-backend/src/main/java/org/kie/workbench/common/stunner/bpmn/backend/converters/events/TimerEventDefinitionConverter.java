package org.kie.workbench.common.stunner.bpmn.backend.converters.events;

import org.eclipse.bpmn2.FormalExpression;
import org.eclipse.bpmn2.TimerEventDefinition;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.timer.TimerSettings;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.timer.TimerSettingsValue;

public class TimerEventDefinitionConverter {

    public static TimerSettings convertTimerEventDefinition(TimerEventDefinition e) {
        TimerSettingsValue timerSettingsValue = new TimerSettings().getValue();
        FormalExpression timeCycle = (FormalExpression) e.getTimeCycle();
        if (timeCycle != null) {
            timerSettingsValue.setTimeCycle(timeCycle.getMixed().getValue(0).toString());
            timerSettingsValue.setTimeCycleLanguage(timeCycle.getLanguage());
        }

        FormalExpression timeDate = (FormalExpression) e.getTimeDate();
        if (timeDate != null) {
            timerSettingsValue.setTimeDate(timeDate.getMixed().getValue(0).toString());
        }

        FormalExpression timeDateDuration = (FormalExpression) e.getTimeDuration();
        if (timeDateDuration != null) {
            timerSettingsValue.setTimeDuration(timeDateDuration.getMixed().getValue(0).toString());
        }
        return new TimerSettings(timerSettingsValue);
    }

}
