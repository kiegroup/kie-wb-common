/*
 * Copyright (C) 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.library.client.settings.knowledgebases.item.knowledgesessions;

import javax.inject.Inject;

import elemental2.dom.HTMLElement;
import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.kie.workbench.common.screens.library.client.settings.util.KieEnumSelectElement;
import org.kie.workbench.common.screens.library.client.settings.util.ListItemPresenter;
import org.kie.workbench.common.screens.library.client.settings.util.UberElementalListItem;
import org.kie.workbench.common.services.shared.kmodule.ClockTypeOption;
import org.kie.workbench.common.services.shared.kmodule.KSessionModel;

public class KnowledgeSessionListItemPresenter extends ListItemPresenter<KSessionModel, KnowledgeSessionsModal, KnowledgeSessionListItemPresenter.View> {

    private final KieEnumSelectElement<ClockTypeOption> clockSelect;
    private KSessionModel kSessionModel;
    private KnowledgeSessionsModal parentPresenter;

    @Inject
    public KnowledgeSessionListItemPresenter(final View view,
                                             final KieEnumSelectElement<ClockTypeOption> clockSelect) {
        super(view);
        this.clockSelect = clockSelect;
    }

    @Override
    public KnowledgeSessionListItemPresenter setup(final KSessionModel kSessionModel,
                                                   final KnowledgeSessionsModal parentPresenter) {
        this.kSessionModel = kSessionModel;
        this.parentPresenter = parentPresenter;

        view.init(this);

        view.setIsDefault(kSessionModel.isDefault());
        view.setName(kSessionModel.getName());
        view.setType(kSessionModel.getType());
        view.setListenersCount(kSessionModel.getListeners().size());
        view.setWorkItemHandlersCount(kSessionModel.getWorkItemHandelerModels().size());

        clockSelect.setup(view.getClockSelectContainer(), ClockTypeOption.values());
        clockSelect.setValue(kSessionModel.getClockType());
        clockSelect.onChange(clockTypeOption -> {
            kSessionModel.setClockType(clockTypeOption);
            parentPresenter.fireChangeEvent();
        });

        return this;
    }

    @Override
    public void remove() {
        super.remove();
        parentPresenter.signalKnowledgeBaseAddedOrRemoved();
    }

    @Override
    public KSessionModel getObject() {
        return kSessionModel;
    }

    public interface View extends UberElementalListItem<KnowledgeSessionListItemPresenter>,
                                  IsElement {

        void setIsDefault(final boolean isDefault);

        void setName(final String name);

        void setType(final String type);

        HTMLElement getClockSelectContainer();

        void setListenersCount(final int listenersCount);

        void setWorkItemHandlersCount(final int workItemHandlersCount);
    }
}
