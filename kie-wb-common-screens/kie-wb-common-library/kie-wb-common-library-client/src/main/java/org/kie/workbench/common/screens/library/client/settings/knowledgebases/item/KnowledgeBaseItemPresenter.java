/*
 * Copyright (C) 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.library.client.settings.knowledgebases.item;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import elemental2.dom.Element;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.screens.library.client.settings.knowledgebases.KnowledgeBasesPresenter;
import org.kie.workbench.common.screens.library.client.settings.knowledgebases.item.includedkbases.IncludedKnowledgeBaseItemPresenter;
import org.kie.workbench.common.screens.library.client.settings.knowledgebases.item.pkg.PackageItemPresenter;
import org.kie.workbench.common.screens.library.client.settings.util.KieEnumSelectElement;
import org.kie.workbench.common.screens.library.client.settings.util.ListItemPresenter;
import org.kie.workbench.common.screens.library.client.settings.util.ListPresenter;
import org.kie.workbench.common.screens.library.client.settings.util.UberElementalListItem;
import org.kie.workbench.common.services.shared.kmodule.AssertBehaviorOption;
import org.kie.workbench.common.services.shared.kmodule.EventProcessingOption;
import org.kie.workbench.common.services.shared.kmodule.KBaseModel;
import org.kie.workbench.common.widgets.client.popups.text.TextBoxFormPopup;

@Dependent
public class KnowledgeBaseItemPresenter extends ListItemPresenter<KBaseModel, KnowledgeBasesPresenter, KnowledgeBaseItemPresenter.View> {

    private final TranslationService translationService;
    private final KieEnumSelectElement<AssertBehaviorOption> equalsBehaviorSelect;
    private final KieEnumSelectElement<EventProcessingOption> eventProcessingModeSelect;
    private final TextBoxFormPopup textBoxFormPopup;
    private final IncludedKnowledgeBasesListPresenter includedKnowledgeBasesListPresenter;
    private final PackageListPresenter packageListPresenter;
    private KBaseModel kBaseModel;

    private KnowledgeBasesPresenter parentPresenter;

    @Inject
    public KnowledgeBaseItemPresenter(final View view,
                                      final TranslationService translationService,
                                      final KieEnumSelectElement<AssertBehaviorOption> equalsBehaviorSelect,
                                      final KieEnumSelectElement<EventProcessingOption> eventProcessingModeSelect,
                                      final TextBoxFormPopup textBoxFormPopup,
                                      final IncludedKnowledgeBasesListPresenter includedKnowledgeBasesListPresenter,
                                      final PackageListPresenter packageListPresenter) {
        super(view);
        this.translationService = translationService;
        this.equalsBehaviorSelect = equalsBehaviorSelect;
        this.eventProcessingModeSelect = eventProcessingModeSelect;
        this.textBoxFormPopup = textBoxFormPopup;
        this.includedKnowledgeBasesListPresenter = includedKnowledgeBasesListPresenter;
        this.packageListPresenter = packageListPresenter;
    }

    @Override
    public KnowledgeBaseItemPresenter setup(final KBaseModel kBaseModel,
                                            final KnowledgeBasesPresenter parentPresenter) {
        this.kBaseModel = kBaseModel;
        this.parentPresenter = parentPresenter;

        view.init(this);

        view.setName(kBaseModel.getName());
        view.setDefault(kBaseModel.isDefault());

        equalsBehaviorSelect.setup(view.getEqualsBehaviorSelectContainer(), AssertBehaviorOption.values());
        equalsBehaviorSelect.setValue(kBaseModel.getEqualsBehavior());
        equalsBehaviorSelect.onChange(equalsBehavior -> {
            kBaseModel.setEqualsBehavior(equalsBehavior);
            fireChangeEvent();
        });

        eventProcessingModeSelect.setup(view.getEventProcessingModelSelectContainer(), EventProcessingOption.values());
        eventProcessingModeSelect.setValue(kBaseModel.getEventProcessingMode());
        eventProcessingModeSelect.onChange(eventProcessingMode -> {
            kBaseModel.setEventProcessingMode(eventProcessingMode);
            fireChangeEvent();
        });

        includedKnowledgeBasesListPresenter.setup(
                view.getIncludedKnowledgeBasesListElement(),
                kBaseModel.getIncludes(),
                (kBaseName, presenter) -> presenter.setup(kBaseName, this));

        packageListPresenter.setup(
                view.getPackagesListElement(),
                kBaseModel.getPackages(),
                (packageName, presenter) -> presenter.setup(packageName, this));

        return this;
    }

    @Override
    public void remove() {
        super.remove();
        fireChangeEvent();
    }

    public void fireChangeEvent() {
        parentPresenter.fireChangeEvent();
    }

    @Override
    public KBaseModel getObject() {
        return kBaseModel;
    }

    public void showNewIncludedKnowledgeBasePopup() {
        textBoxFormPopup.show(kBaseName -> {
            includedKnowledgeBasesListPresenter.add(kBaseName);
            parentPresenter.fireChangeEvent();
        });
    }

    public void showNewPackagePopup() {
        textBoxFormPopup.show(packageName -> {
            packageListPresenter.add(packageName);
            parentPresenter.fireChangeEvent();
        });
    }

    public void setDefault(final boolean isDefault) {
        this.kBaseModel.setDefault(isDefault);
    }

    public interface View extends UberElementalListItem<KnowledgeBaseItemPresenter>,
                                  IsElement {

        void setName(final String name);

        Element getPackagesListElement();

        Element getIncludedKnowledgeBasesListElement();

        void setDefault(boolean isDefault);

        Element getEqualsBehaviorSelectContainer();

        Element getEventProcessingModelSelectContainer();
    }

    @Dependent
    public static class IncludedKnowledgeBasesListPresenter extends ListPresenter<String, IncludedKnowledgeBaseItemPresenter> {

        @Inject
        public IncludedKnowledgeBasesListPresenter(ManagedInstance<IncludedKnowledgeBaseItemPresenter> itemPresenters) {
            super(itemPresenters);
        }
    }

    @Dependent
    public static class PackageListPresenter extends ListPresenter<String, PackageItemPresenter> {

        @Inject
        public PackageListPresenter(ManagedInstance<PackageItemPresenter> itemPresenters) {
            super(itemPresenters);
        }
    }
}
