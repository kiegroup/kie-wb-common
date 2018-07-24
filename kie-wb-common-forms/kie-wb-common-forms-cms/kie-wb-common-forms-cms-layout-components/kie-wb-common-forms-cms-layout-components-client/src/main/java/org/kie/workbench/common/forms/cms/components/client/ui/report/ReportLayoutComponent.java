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

package org.kie.workbench.common.forms.cms.components.client.ui.report;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PreDestroy;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.forms.cms.components.client.resources.i18n.CMSComponentsConstants;
import org.kie.workbench.common.forms.cms.components.client.ui.AbstractFormsCMSLayoutComponent;
import org.kie.workbench.common.forms.cms.components.client.ui.report.entry.ReportEntry;
import org.kie.workbench.common.forms.cms.components.client.ui.settings.SettingsDisplayer;
import org.kie.workbench.common.forms.cms.components.service.shared.RenderingContextGenerator;
import org.kie.workbench.common.forms.cms.components.shared.model.report.ReportSettings;
import org.kie.workbench.common.forms.cms.persistence.shared.PersistenceService;
import org.kie.workbench.common.forms.cms.persistence.shared.PersistentInstance;
import org.kie.workbench.common.forms.dynamic.service.shared.FormRenderingContext;
import org.kie.workbench.common.forms.dynamic.service.shared.RenderMode;
import org.kie.workbench.common.forms.dynamic.service.shared.impl.MapModelRenderingContext;
import org.kie.workbench.common.forms.model.FormDefinition;

public class ReportLayoutComponent extends AbstractFormsCMSLayoutComponent<ReportSettings, ReportSettingsReader> implements ReportLayoutComponentView.Presenter {

    private ManagedInstance<ReportEntry> reportEntries;
    private List<Map<String, Object>> values;
    private ReportLayoutComponentView view;
    private FormRenderingContext originalContext;

    @Inject
    public ReportLayoutComponent(TranslationService translationService,
                                 SettingsDisplayer settingsDisplayer,
                                 ReportSettingsReader reader,
                                 Caller<RenderingContextGenerator> contextGenerator,
                                 ManagedInstance<ReportEntry> reportEntries,
                                 Caller<PersistenceService> persistenceService,
                                 ReportLayoutComponentView view) {
        super(translationService,
              settingsDisplayer,
              reader,
              persistenceService,
              contextGenerator);
        this.reportEntries = reportEntries;
        this.view = view;
        view.init(this);
    }

    @Override
    protected IsWidget getWidget() {
        view.clear();
        if (checkSettings()) {
            contextGenerator.call((RemoteCallback<FormRenderingContext>) contextResponse -> {

                if (contextResponse == null) {
                    return;
                }

                originalContext = contextResponse;

                persistenceService.call((RemoteCallback<List<PersistentInstance>>) persistentModels -> {

                    List<Map<String, Object>> result = persistentModels.stream().map(PersistentInstance::getModel).collect(Collectors.toList());
                    result.forEach(instance -> {

                        MapModelRenderingContext tableContext = getTableContext(instance);
                        MapModelRenderingContext previewContext = getPreviewContext(instance);

                        ReportEntry entry = reportEntries.get();

                        entry.init(instance, tableContext, previewContext);

                        view.append(entry);
                    });
                }).query(settings.getDataObject());
            }).generateContext(settings);
        }
        return ElementWrapperWidget.getWidget(view.getElement());
    }

    private MapModelRenderingContext getPreviewContext(Map<String, Object> instance) {
        return getRenderingContext(settings.getPreviewForm(), instance);
    }

    private MapModelRenderingContext getTableContext(Map<String, Object> instance) {
        return getRenderingContext(settings.getTableForm(), instance);
    }

    private MapModelRenderingContext getRenderingContext(String formId,
                                                         Map<String, Object> instance) {

        MapModelRenderingContext context = new MapModelRenderingContext(String.valueOf(System.currentTimeMillis()));
        context.setModel(instance);
        context.setRootForm((FormDefinition) originalContext.getAvailableForms().get(formId));
        context.getAvailableForms().putAll(originalContext.getAvailableForms());
        context.setRenderMode(RenderMode.PRETTY_MODE);

        return context;
    }

    @Override
    public String getDragComponentTitle() {
        return translationService.getTranslation(CMSComponentsConstants.ReportLayoutComponentTitle);
    }

    @Override
    protected boolean checkSettings() {
        return super.checkSettings() && settings.getPreviewForm() != null && settings.getTableForm() != null;
    }

    @PreDestroy
    public void destroy() {
        reportEntries.destroyAll();
    }
}
