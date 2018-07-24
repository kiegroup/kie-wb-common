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

package org.kie.workbench.common.forms.cms.components.client.ui;

import java.util.Map;

import com.google.gwt.user.client.ui.IsWidget;
import org.gwtbootstrap3.client.ui.Modal;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.forms.cms.components.client.ui.settings.SettingsDisplayer;
import org.kie.workbench.common.forms.cms.components.service.shared.RenderingContextGenerator;
import org.kie.workbench.common.forms.cms.components.shared.model.BasicComponentSettings;
import org.kie.workbench.common.forms.cms.persistence.shared.PersistenceService;
import org.uberfire.ext.layout.editor.client.api.HasModalConfiguration;
import org.uberfire.ext.layout.editor.client.api.ModalConfigurationContext;
import org.uberfire.ext.layout.editor.client.api.RenderingContext;

public abstract class AbstractFormsCMSLayoutComponent<SETTINGS extends BasicComponentSettings, READER extends SettingsReader<SETTINGS>> implements DataManagementDragComponent,
                                                                                                                                                   HasModalConfiguration {

    protected TranslationService translationService;
    protected SettingsDisplayer settingsDisplayer;
    protected Caller<RenderingContextGenerator> contextGenerator;
    protected Caller<PersistenceService> persistenceService;

    protected READER reader;

    protected SETTINGS settings;

    public AbstractFormsCMSLayoutComponent(TranslationService translationService,
                                           SettingsDisplayer settingsDisplayer,
                                           READER reader,
                                           Caller<PersistenceService> persistenceService,
                                           Caller<RenderingContextGenerator> contextGenerator) {
        this.translationService = translationService;
        this.settingsDisplayer = settingsDisplayer;
        this.reader = reader;
        this.persistenceService = persistenceService;
        this.contextGenerator = contextGenerator;
    }

    @Override
    public IsWidget getShowWidget(RenderingContext ctx) {
        return getWidget(ctx);
    }

    @Override
    public IsWidget getPreviewWidget(RenderingContext ctx) {
        return getWidget(ctx);
    }

    @Override
    public Modal getConfigurationModal(final ModalConfigurationContext ctx) {
        settings = reader.fromMap(ctx.getComponentProperties());

        settingsDisplayer.init(settings,
                               () -> {
                                   Map<String, String> settingsMap = reader.toMap(settings);
                                   settingsMap.forEach((key, value) -> ctx.setComponentProperty(key,
                                                                                                value));
                                   getWidget();
                                   ctx.configurationFinished();
                               },
                               () -> {
                                   settings = reader.fromMap(ctx.getComponentProperties());
                                   ctx.configurationCancelled();
                               });

        return settingsDisplayer.getView().getPropertiesModal();
    }

    protected IsWidget getWidget(RenderingContext ctx) {

        settings = reader.fromMap(ctx.getComponent().getProperties());

        return getWidget();
    }

    protected boolean checkSettings() {
        return settings.getOu() != null && settings.getProject() != null && settings.getDataObject() != null;
    }

    protected abstract IsWidget getWidget();
}
