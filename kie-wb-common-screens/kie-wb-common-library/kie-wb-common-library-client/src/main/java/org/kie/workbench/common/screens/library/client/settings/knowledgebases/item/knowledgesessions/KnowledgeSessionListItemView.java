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
import javax.inject.Named;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import elemental2.dom.HTMLAnchorElement;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLInputElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Templated("#root")
public class KnowledgeSessionListItemView implements KnowledgeSessionListItemPresenter.View {

    @Inject
    @DataField("is-default")
    private HTMLInputElement isDefault;

    @Inject
    @DataField("name")
    private HTMLInputElement name;

    @Inject
    @DataField("type")
    private HTMLInputElement type;

    @Inject
    @DataField("clock-select-container")
    private HTMLDivElement clockSelectContainer;

    @Inject
    @DataField("listeners-link")
    private HTMLAnchorElement listenersLink;

    @Inject
    @Named("strong")
    @DataField("listeners-count")
    private HTMLElement listenersCount;

    @Inject
    @DataField("work-item-handlers-link")
    private HTMLAnchorElement workItemHandlersLink;

    @Inject
    @Named("strong")
    @DataField("work-item-handlers-count")
    private HTMLElement workItemHandlersCount;

    @Inject
    @DataField("remove-button")
    private HTMLAnchorElement removeButton;

    private KnowledgeSessionListItemPresenter presenter;

    @Override
    public void init(final KnowledgeSessionListItemPresenter presenter) {
        this.presenter = presenter;
    }

    @EventHandler("name")
    public void onNameChanged(final ChangeEvent ignore) {
        this.presenter.setName(name.value);
    }

    @EventHandler("type")
    public void onTypeChanged(final ChangeEvent ignore) {
        this.presenter.setType(type.value);
    }

    @EventHandler("remove-button")
    public void onRemoveButtonClicked(final ClickEvent ignore) {
        this.presenter.remove();
    }

    @Override
    public void setIsDefault(final boolean isDefault) {
        this.isDefault.checked = isDefault;
    }

    @Override
    public void setName(final String name) {
        this.name.value = name;
    }

    @Override
    public void setType(final String type) {
        this.type.value = type;
    }

    @Override
    public void setListenersCount(final int listenersCount) {
        this.listenersCount.textContent = Integer.toString(listenersCount);
    }

    @Override
    public void setWorkItemHandlersCount(final int workItemHandlersCount) {
        this.workItemHandlersCount.textContent = Integer.toString(workItemHandlersCount);
    }

    @Override
    public HTMLElement getClockSelectContainer() {
        return clockSelectContainer;
    }
}
