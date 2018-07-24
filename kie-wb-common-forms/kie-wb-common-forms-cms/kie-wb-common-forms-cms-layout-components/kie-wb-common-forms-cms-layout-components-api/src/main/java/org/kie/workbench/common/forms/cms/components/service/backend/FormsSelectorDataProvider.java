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

package org.kie.workbench.common.forms.cms.components.service.backend;

import java.util.Map;
import java.util.stream.Collectors;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.forms.cms.components.shared.model.BasicComponentSettings;
import org.kie.workbench.common.forms.dynamic.model.config.SelectorData;
import org.kie.workbench.common.forms.dynamic.model.config.SelectorDataProvider;
import org.kie.workbench.common.forms.dynamic.service.shared.FormRenderingContext;
import org.kie.workbench.common.forms.model.FormDefinition;

@Dependent
public class FormsSelectorDataProvider implements SelectorDataProvider {

    private ProvidersHelperService providersHelperService;

    @Inject
    public FormsSelectorDataProvider(ProvidersHelperService providersHelperService) {
        this.providersHelperService = providersHelperService;
    }

    @Override
    public String getProviderName() {
        return getClass().getName();
    }

    @Override
    public SelectorData getSelectorData(FormRenderingContext context) {

        BasicComponentSettings settings = (BasicComponentSettings) context.getModel();

        String ou = settings.getOu();
        String project = settings.getProject();
        String dataObject = settings.getDataObject();

        Map<String, String> values = providersHelperService.getFormsForDataObject(ou,
                                                                                  project,
                                                                                  dataObject).stream().collect(Collectors.toMap(FormDefinition::getId,
                                                                                                                                FormDefinition::getName));

        return new SelectorData(values,
                                null);
    }
}
