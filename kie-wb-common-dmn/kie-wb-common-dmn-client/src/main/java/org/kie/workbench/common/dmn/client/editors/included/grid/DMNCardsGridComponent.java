/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.editors.included.grid;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import elemental2.dom.HTMLElement;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.dmn.client.editors.common.cards.CardComponent;
import org.kie.workbench.common.dmn.client.editors.common.cards.CardsGridComponent;
import org.kie.workbench.common.dmn.client.editors.included.IncludedModel;
import org.kie.workbench.common.dmn.client.editors.included.IncludedModelsPageState;

public class DMNCardsGridComponent {

    private final ManagedInstance<DMNCardComponent> dmnCardComponent;

    private final CardsGridComponent cardsGridComponent;

    private final IncludedModelsPageState pageState;

    @Inject
    public DMNCardsGridComponent(final ManagedInstance<DMNCardComponent> dmnCardComponent,
                                 final CardsGridComponent cardsGridComponent,
                                 final IncludedModelsPageState pageState) {
        this.dmnCardComponent = dmnCardComponent;
        this.cardsGridComponent = cardsGridComponent;
        this.pageState = pageState;
    }

    public void refresh() {
        cardsGridComponent.setupCards(dmnCards(makeIncludedModels()));
    }

    public HTMLElement getElement() {
        return cardsGridComponent.getElement();
    }

    private List<CardComponent> dmnCards(final List<IncludedModel> includes) {
        return includes.stream().map(this::dmnCard).collect(Collectors.toList());
    }

    private DMNCardComponent dmnCard(final IncludedModel includedModel) {
        final DMNCardComponent card = dmnCardComponent.get();
        card.setup(this, includedModel);
        return card;
    }

    private List<IncludedModel> makeIncludedModels() {
        return pageState.generateIncludedModels();
    }
}
