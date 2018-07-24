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

package org.kie.workbench.common.forms.cms.components.service.backend.impl;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.bus.server.annotations.Service;
import org.kie.workbench.common.forms.cms.components.service.backend.ProvidersHelperService;
import org.kie.workbench.common.forms.cms.components.service.shared.RenderingContextGenerator;
import org.kie.workbench.common.forms.cms.components.shared.model.crud.CRUDSettings;
import org.kie.workbench.common.forms.cms.components.shared.model.report.ReportSettings;
import org.kie.workbench.common.forms.cms.components.shared.model.wizard.WizardSettings;
import org.kie.workbench.common.forms.dynamic.service.shared.FormRenderingContext;
import org.kie.workbench.common.forms.dynamic.service.shared.impl.MapModelRenderingContext;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.HasNestedForm;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.IsCRUDDefinition;
import org.kie.workbench.common.forms.model.FormDefinition;

@Service
@Dependent
public class RenderingContextGeneratorImpl implements RenderingContextGenerator {

    private ProvidersHelperService providersHelperService;

    @Inject
    public RenderingContextGeneratorImpl(ProvidersHelperService providersHelperService) {
        this.providersHelperService = providersHelperService;
    }

    @Override
    public FormRenderingContext generateContext(String ouId,
                                                String projectName,
                                                String formId) {

        FormDefinition formDefinition = providersHelperService.getFormById(ouId,
                                                                           projectName,
                                                                           formId);

        if (formDefinition != null) {
            FormRenderingContext context = new MapModelRenderingContext(String.valueOf(System.currentTimeMillis()));

            context.setRootForm(formDefinition);

            initAllForms(ouId,
                         projectName,
                         formDefinition,
                         context);

            return context;
        }

        return null;
    }

    @Override
    public FormRenderingContext generateContext(CRUDSettings crudSettings) {
        FormDefinition formDefinition = providersHelperService.getFormById(crudSettings.getOu(), crudSettings.getProject(), crudSettings.getCreationForm());

        if(formDefinition != null) {
            FormRenderingContext context = new MapModelRenderingContext(String.valueOf(System.currentTimeMillis()));

            context.setRootForm(formDefinition);

            initAllForms(crudSettings.getOu(),
                         crudSettings.getProject(),
                         formDefinition,
                         context);

            if(!context.getAvailableForms().containsKey(crudSettings.getEditionForm())) {
                formDefinition = providersHelperService.getFormById(crudSettings.getOu(), crudSettings.getProject(), crudSettings.getCreationForm());
                if(formDefinition != null) {
                    context.getAvailableForms().put(formDefinition.getId(), formDefinition);
                    initAllForms(crudSettings.getOu(),
                                 crudSettings.getProject(),
                                 formDefinition,
                                 context);
                }
            }

            if(!context.getAvailableForms().containsKey(crudSettings.getPreviewForm())) {
                formDefinition = providersHelperService.getFormById(crudSettings.getOu(), crudSettings.getProject(), crudSettings.getCreationForm());
                if(formDefinition != null) {
                    context.getAvailableForms().put(formDefinition.getId(), formDefinition);
                    initAllForms(crudSettings.getOu(),
                                 crudSettings.getProject(),
                                 formDefinition,
                                 context);
                }
            }

            if(!context.getAvailableForms().containsKey(crudSettings.getTableForm())) {
                formDefinition = providersHelperService.getFormById(crudSettings.getOu(), crudSettings.getProject(), crudSettings.getCreationForm());
                if(formDefinition != null) {
                    context.getAvailableForms().put(formDefinition.getId(), formDefinition);
                    initAllForms(crudSettings.getOu(),
                                 crudSettings.getProject(),
                                 formDefinition,
                                 context);
                }
            }
            return context;
        }

        return null;
    }

    @Override
    public FormRenderingContext generateContext(ReportSettings reportSettings) {
        FormDefinition formDefinition = providersHelperService.getFormById(reportSettings.getOu(), reportSettings.getProject(), reportSettings.getTableForm());

        if(formDefinition != null) {
            FormRenderingContext context = new MapModelRenderingContext(String.valueOf(System.currentTimeMillis()));

            context.setRootForm(formDefinition);

            initAllForms(reportSettings.getOu(),
                         reportSettings.getProject(),
                         formDefinition,
                         context);

            if(!context.getAvailableForms().containsKey(reportSettings.getPreviewForm())) {
                formDefinition = providersHelperService.getFormById(reportSettings.getOu(), reportSettings.getProject(), reportSettings.getPreviewForm());
                if(formDefinition != null) {
                    context.getAvailableForms().put(formDefinition.getId(), formDefinition);
                    initAllForms(reportSettings.getOu(),
                                 reportSettings.getProject(),
                                 formDefinition,
                                 context);
                }
            }
            return context;
        }
        return null;
    }

    @Override
    public FormRenderingContext generateContext(WizardSettings settings) {

        MapModelRenderingContext context = new MapModelRenderingContext(String.valueOf(System.currentTimeMillis()));

        settings.getSteps().forEach(wizardStep -> {
            FormDefinition formDefinition = providersHelperService.getFormById(settings.getOu(), settings.getProject(), wizardStep.getForm());

            if(formDefinition != null && !context.getAvailableForms().containsKey(formDefinition.getId())) {
                context.getAvailableForms().put(formDefinition.getId(), formDefinition);
                initAllForms(settings.getOu(),
                             settings.getProject(),
                             formDefinition,
                             context);
            }
        });

        return context;
    }

    protected void initAllForms(String ouId,
                                String projectName,
                                FormDefinition formDefinition,
                                FormRenderingContext context) {

        formDefinition.getFields().forEach(fieldDefinition -> {
            if (fieldDefinition instanceof HasNestedForm) {
                HasNestedForm nestedFormField = (HasNestedForm) fieldDefinition;

                lookupFormDefinition(ouId,
                                     projectName,
                                     nestedFormField.getNestedForm(),
                                     context);
            } else if (fieldDefinition instanceof IsCRUDDefinition) {
                IsCRUDDefinition isCRUDDefinition = (IsCRUDDefinition) fieldDefinition;

                lookupFormDefinition(ouId,
                                     projectName,
                                     isCRUDDefinition.getCreationForm(),
                                     context);

                lookupFormDefinition(ouId,
                                     projectName,
                                     isCRUDDefinition.getEditionForm(),
                                     context);
            }
        });
    }

    protected void lookupFormDefinition(String ouId,
                                        String projectName,
                                        String formId,
                                        FormRenderingContext context) {

        if (!context.getAvailableForms().containsKey(formId)) {
            FormDefinition formDefinition = providersHelperService.getFormById(ouId,
                                                                               projectName,
                                                                               formId);

            context.getAvailableForms().put(formDefinition.getId(),
                                            formDefinition);

            initAllForms(ouId,
                         projectName,
                         formDefinition,
                         context);
        }
    }
}
