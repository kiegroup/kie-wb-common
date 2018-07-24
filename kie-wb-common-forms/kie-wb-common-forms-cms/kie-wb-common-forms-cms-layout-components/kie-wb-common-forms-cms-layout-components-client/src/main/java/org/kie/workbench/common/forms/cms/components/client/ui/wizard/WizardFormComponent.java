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

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.gwtbootstrap3.extras.notify.client.constants.NotifyType;
import org.gwtbootstrap3.extras.notify.client.ui.Notify;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.forms.cms.components.client.resources.i18n.CMSComponentsConstants;
import org.kie.workbench.common.forms.cms.components.client.ui.AbstractFormsCMSLayoutComponent;
import org.kie.workbench.common.forms.cms.components.client.ui.settings.SettingsDisplayer;
import org.kie.workbench.common.forms.cms.components.client.ui.wizard.ui.WizardForm;
import org.kie.workbench.common.forms.cms.components.client.ui.wizard.ui.WizardFormStep;
import org.kie.workbench.common.forms.cms.components.service.shared.RenderingContextGenerator;
import org.kie.workbench.common.forms.cms.components.shared.model.wizard.WizardSettings;
import org.kie.workbench.common.forms.cms.components.shared.model.wizard.WizardStep;
import org.kie.workbench.common.forms.cms.persistence.shared.InstanceCreationResponse;
import org.kie.workbench.common.forms.cms.persistence.shared.OperationResult;
import org.kie.workbench.common.forms.cms.persistence.shared.PersistenceService;
import org.kie.workbench.common.forms.cms.persistence.shared.PersistentInstance;
import org.kie.workbench.common.forms.dynamic.service.shared.FormRenderingContext;
import org.kie.workbench.common.forms.dynamic.service.shared.impl.MapModelRenderingContext;
import org.kie.workbench.common.forms.model.FormDefinition;

@Dependent
public class WizardFormComponent extends AbstractFormsCMSLayoutComponent<WizardSettings, WizardSettingsReader> {

    private WizardForm wizardForm;

    private FormRenderingContext originalContext;

    @Inject
    public WizardFormComponent(TranslationService translationService,
                               SettingsDisplayer settingsDisplayer,
                               WizardSettingsReader reader,
                               Caller<PersistenceService> persistenceService,
                               Caller<RenderingContextGenerator> contextGenerator,
                               WizardForm wizardForm) {
        super(translationService,
              settingsDisplayer,
              reader,
              persistenceService,
              contextGenerator);
        this.wizardForm = wizardForm;
    }

    @Override
    protected IsWidget getWidget() {

        if (checkSettings()) {
            contextGenerator.call((RemoteCallback<FormRenderingContext>) context -> {
                originalContext = context;

                List<WizardFormStep> wizardFormSteps = settings.getSteps().stream().map(WizardFormComponent.this::convert).collect(Collectors.toList());

                wizardForm.init(wizardFormSteps, WizardFormComponent.this::persist);
            }).generateContext(settings);
        }

        return ElementWrapperWidget.getWidget(wizardForm.getElement());
    }

    private WizardFormStep convert(WizardStep step) {

        MapModelRenderingContext context = getRenderingContext(step.getForm());

        WizardFormStep wizardFormStep = new WizardFormStep(step.getTitle(), context);

        return wizardFormStep;
    }

    private MapModelRenderingContext getRenderingContext(String formId) {

        MapModelRenderingContext context = new MapModelRenderingContext(String.valueOf(System.currentTimeMillis()));
        context.setRootForm((FormDefinition) originalContext.getAvailableForms().get(formId));
        context.getAvailableForms().putAll(originalContext.getAvailableForms());

        return context;
    }

    private void persist(Map<String, Object> instance) {
        persistenceService.call((RemoteCallback<InstanceCreationResponse>) persistenceResponse -> {
            if (OperationResult.SUCCESS.equals(persistenceResponse.getResult())) {
                Notify.notify(translationService.getTranslation(CMSComponentsConstants.ObjectCreationComponentConfirmation));
            } else {
                Notify.notify(translationService.getTranslation(CMSComponentsConstants.PersistenceErrorMessage), NotifyType.WARNING);
            }
        }).createInstance(new PersistentInstance(null, settings.getDataObject(), instance));
    }

    @Override
    public String getDragComponentTitle() {
        return translationService.getTranslation(CMSComponentsConstants.WizardFormComponentTitle);
    }

    @Override
    protected boolean checkSettings() {
        return super.checkSettings() && checkSteps();
    }

    private boolean checkSteps() {
        return settings.getSteps() != null && !settings.getSteps().isEmpty();
    }
}
