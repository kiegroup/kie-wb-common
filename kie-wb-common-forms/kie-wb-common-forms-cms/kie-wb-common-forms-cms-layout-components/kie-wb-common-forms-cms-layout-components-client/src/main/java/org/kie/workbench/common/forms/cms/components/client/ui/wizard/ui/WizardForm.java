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

package org.kie.workbench.common.forms.cms.components.client.ui.wizard.ui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.databinding.client.BindableProxy;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.soup.commons.validation.PortablePreconditions;
import org.kie.workbench.common.forms.dynamic.client.DynamicFormRenderer;
import org.kie.workbench.common.forms.dynamic.service.shared.FormRenderingContext;
import org.uberfire.mvp.ParameterizedCommand;

@Dependent
public class WizardForm implements IsElement,
                                   WizardFormView.Presenter {

    private WizardFormView view;

    private ManagedInstance<DynamicFormRenderer> renderers;

    private List<WizardFormStep> steps;

    private ParameterizedCommand<Map<String, Object>> onFinish;

    private WizardFormStep currentStep;

    private DynamicFormRenderer currentRenderer;

    private Map<String, Object> model;

    @Inject
    public WizardForm(WizardFormView view,
                      ManagedInstance<DynamicFormRenderer> renderers) {
        this.view = view;
        this.renderers = renderers;
        view.init(this);
    }

    public void init(List<WizardFormStep> steps,
                     ParameterizedCommand<Map<String, Object>> onFinish) {
        PortablePreconditions.checkNotNull("steps",
                                           steps);
        PortablePreconditions.checkNotNull("onFinish",
                                           onFinish);

        this.steps = steps;
        this.onFinish = onFinish;

        start();
    }

    protected void start() {
        view.clear();

        model = new HashMap<>();

        currentRenderer = null;

        renderStep(steps.get(0));
    }

    protected void renderStep(WizardFormStep step) {
        this.currentStep = step;

        if (currentRenderer != null) {
            model.putAll(((BindableProxy<Map<String, Object>>) currentRenderer.getModel()).deepUnwrap());
        }

        DynamicFormRenderer newRenderer = renderers.get();

        FormRenderingContext context = currentStep.getContext();

        context.setModel(model);

        newRenderer.render(context);

        view.renderStep(steps.indexOf(step) + 1,
                        step.getTitle(),
                        newRenderer);

        if (currentRenderer != null) {
            renderers.destroy(currentRenderer);
        }
        currentRenderer = newRenderer;
    }

    @Override
    public HTMLElement getElement() {
        return view.getElement();
    }

    @Override
    public boolean isFirst() {
        return steps.indexOf(currentStep) == 0;
    }

    @Override
    public boolean isLast() {
        return steps.indexOf(currentStep) == steps.size() - 1;
    }

    @Override
    public void previousStep() {
        if (!isFirst()) {
            renderStep(steps.get(steps.indexOf(currentStep) - 1));
        }
    }

    @Override
    public void nextStep() {
        if (!isLast() && currentRenderer.isValid()) {
            renderStep(steps.get(steps.indexOf(currentStep) + 1));
        }
    }

    @Override
    public void cancel() {
        start();
    }

    @Override
    public void finish() {
        model.putAll(((BindableProxy<Map<String, Object>>) currentRenderer.getModel()).deepUnwrap());

        onFinish.execute(model);
        start();
    }

    @PreDestroy
    public void destroy() {
        renderers.destroyAll();
    }
}
