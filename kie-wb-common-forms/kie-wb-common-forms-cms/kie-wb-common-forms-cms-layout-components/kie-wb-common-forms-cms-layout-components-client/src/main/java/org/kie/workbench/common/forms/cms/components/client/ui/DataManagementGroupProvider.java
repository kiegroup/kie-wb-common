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
package org.kie.workbench.common.forms.cms.components.client.ui;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.forms.cms.components.client.resources.i18n.CMSComponentsConstants;
import org.kie.workbench.common.forms.cms.components.client.ui.report.ReportLayoutComponent;
import org.kie.workbench.common.forms.cms.components.service.shared.DataManagementFeatureDefinition;
import org.uberfire.ext.experimental.client.service.ClientExperimentalFeaturesRegistryService;
import org.uberfire.ext.layout.editor.client.api.LayoutDragComponentGroup;
import org.uberfire.ext.plugin.client.perspective.editor.api.PerspectiveEditorComponentGroupProvider;

/**
 * {@link PerspectiveEditorComponentGroupProvider} holding all the available {@link DataManagementDragComponent} instances
 */
@ApplicationScoped
public class DataManagementGroupProvider implements PerspectiveEditorComponentGroupProvider {

    private static final String[] BANNED_COMPONENTS = {ReportLayoutComponent.class.getName()};

    private SyncBeanManager beanManager;
    private TranslationService translationService;
    private ClientExperimentalFeaturesRegistryService experimentalFeaturesRegistryService;

    @Inject
    public DataManagementGroupProvider(SyncBeanManager beanManager, TranslationService translationService, ClientExperimentalFeaturesRegistryService experimentalFeaturesRegistryService) {
        this.beanManager = beanManager;
        this.translationService = translationService;
        this.experimentalFeaturesRegistryService = experimentalFeaturesRegistryService;
    }

    @Override
    public String getName() {
        return translationService.getTranslation(CMSComponentsConstants.DataManagementGroupName);
    }

    @Override
    public LayoutDragComponentGroup getInstance() {
        LayoutDragComponentGroup group = new LayoutDragComponentGroup(getName());

        List<String> bannedComponents = Arrays.asList(BANNED_COMPONENTS);

        Collection<SyncBeanDef<DataManagementDragComponent>> beanDefs = beanManager.lookupBeans(DataManagementDragComponent.class);

        beanDefs.stream()
                .filter(beanDef -> !bannedComponents.contains(beanDef.getBeanClass().getName()))
                .forEach(beanDef -> {
                    DataManagementDragComponent dragComponent = beanDef.getInstance();
                    group.addLayoutDragComponent(dragComponent.getDragComponentTitle(), dragComponent);
                });

        return group;
    }

    @Override
    public boolean isEnabled() {
        return experimentalFeaturesRegistryService.isFeatureEnabled(DataManagementFeatureDefinition.DATA_MANAGEMENT_FEATURE_KEY);
    }
}
