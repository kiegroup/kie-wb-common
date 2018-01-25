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
