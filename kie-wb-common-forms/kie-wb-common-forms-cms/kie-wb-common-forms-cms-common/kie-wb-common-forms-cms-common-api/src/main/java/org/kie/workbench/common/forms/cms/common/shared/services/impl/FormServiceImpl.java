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

package org.kie.workbench.common.forms.cms.common.shared.services.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

import org.kie.workbench.common.forms.cms.common.shared.events.FormsDeployedEvent;
import org.kie.workbench.common.forms.cms.common.shared.services.FormService;
import org.kie.workbench.common.forms.model.FormDefinition;
import org.kie.workbench.common.forms.model.JavaFormModel;

@ApplicationScoped
public class FormServiceImpl implements FormService {

    private List<FormDefinition> deployedFormDefinitions = new ArrayList<>();

    @Override
    public FormDefinition getFormById(final String formId) {
        return getFormBy(formDefinition -> formDefinition.getId().equals(formId));
    }

    @Override
    public FormDefinition getFormByModelType(final String modelType) {
        return getFormBy(formDefinition -> {
            if(formDefinition.getModel() instanceof JavaFormModel) {
                return ((JavaFormModel)formDefinition.getModel()).getType().equals(modelType);
            }
            return false;
        });
    }

    private FormDefinition getFormBy(Predicate<FormDefinition> predicate) {
        return deployedFormDefinitions.stream().filter(predicate).findFirst().orElse(null);
    }

    @Override
    public void registerForms(List<FormDefinition> formDefinitions) {
        if(formDefinitions != null) {
            deployedFormDefinitions.addAll(formDefinitions);
        }
    }

    public void onDeploy(@Observes FormsDeployedEvent event) {
        registerForms(event.getFormDefinitions());
    }
}
