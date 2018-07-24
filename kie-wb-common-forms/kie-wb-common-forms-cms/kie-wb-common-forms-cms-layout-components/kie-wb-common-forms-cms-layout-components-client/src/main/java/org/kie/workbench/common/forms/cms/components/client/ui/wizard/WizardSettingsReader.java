/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.forms.cms.components.client.ui.wizard;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.enterprise.context.Dependent;

import org.kie.workbench.common.forms.cms.components.client.ui.SettingsReader;
import org.kie.workbench.common.forms.cms.components.shared.model.wizard.WizardSettings;
import org.kie.workbench.common.forms.cms.components.shared.model.wizard.WizardStep;

@Dependent
public class WizardSettingsReader extends SettingsReader<WizardSettings> {

    public static final String STEP = "step.";

    public static final String STEP_FORM = ".form";
    public static final String STEP_TITLE = ".title";

    @Override
    protected Map<String, String> writeToMap(WizardSettings settings) {
        Map<String, String> settingsMap = new HashMap<>();

        for (int i = 0; i < settings.getSteps().size(); i++) {
            settingsMap.put(STEP + i + STEP_FORM,
                            settings.getSteps().get(i).getForm());
            settingsMap.put(STEP + i + STEP_TITLE,
                            settings.getSteps().get(i).getTitle());
        }

        return settingsMap;
    }

    @Override
    protected WizardSettings readFromMap(Map<String, String> settingsMap) {
        TreeMap<Integer, WizardStep> steps = new TreeMap<>();

        settingsMap.entrySet().forEach(entry -> {
            String key = entry.getKey();

            if (entry.getKey().startsWith(STEP)) {
                String sIndex = key.substring(key.indexOf(".") + 1, key.lastIndexOf("."));

                WizardStep step = steps.get(Integer.decode(sIndex));

                if (step == null) {
                    step = new WizardStep();
                    steps.put(Integer.decode(sIndex), step);
                }

                if (key.endsWith(STEP_FORM)) {
                    step.setForm(entry.getValue());
                } else {
                    step.setTitle(entry.getValue());
                }
            }
        });

        WizardSettings settings = new WizardSettings();

        settings.setSteps(steps.values().stream().collect(Collectors.toList()));

        return settings;
    }
}
