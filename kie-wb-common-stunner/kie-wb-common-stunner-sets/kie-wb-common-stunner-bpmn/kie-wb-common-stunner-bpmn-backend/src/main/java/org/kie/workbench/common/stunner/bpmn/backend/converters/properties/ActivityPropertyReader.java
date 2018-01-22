package org.kie.workbench.common.stunner.bpmn.backend.converters.properties;

import org.eclipse.bpmn2.Activity;

public class ActivityPropertyReader extends ElementPropertyReader {
    private final Activity activity;

    public ActivityPropertyReader(Activity activity) {
        super(activity);
        this.activity = activity;
    }

    public boolean findInputBooleans(String name) {
        return Properties.findInputBooleans(activity.getDataInputAssociations(), name);
    }

    public String findInputValue(String name) {
        return Properties.findInputValue(activity.getDataInputAssociations(), name);
    }
}
