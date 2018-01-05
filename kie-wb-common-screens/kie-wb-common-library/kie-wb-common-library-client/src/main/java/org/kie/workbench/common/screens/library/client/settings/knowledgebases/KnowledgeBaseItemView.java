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

package org.kie.workbench.common.screens.library.client.settings.knowledgebases;

import javax.inject.Inject;
import javax.inject.Named;

import elemental2.dom.HTMLElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Templated
public class KnowledgeBaseItemView implements KnowledgeBaseItemPresenter.View {

    @Inject
    @Named("span")
    @DataField("name")
    private HTMLElement name;

    private KnowledgeBaseItemPresenter presenter;

    @Override
    public void init(final KnowledgeBaseItemPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setName(final String name) {
        this.name.textContent = name;
    }
}
