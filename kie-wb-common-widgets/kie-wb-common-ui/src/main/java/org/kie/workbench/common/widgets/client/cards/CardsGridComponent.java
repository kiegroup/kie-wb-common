/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.widgets.client.cards;

import java.util.List;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import elemental2.dom.HTMLElement;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.kie.workbench.common.widgets.client.cards.frame.CardFrameComponent;
import org.uberfire.client.mvp.UberElemental;

public class CardsGridComponent {

    private final View view;

    private final ManagedInstance<CardFrameComponent> frames;

    private HTMLElement emptyStateElement;

    @Inject
    public CardsGridComponent(final View view,
                              final ManagedInstance<CardFrameComponent> frames) {
        this.view = view;
        this.frames = frames;
    }

    @PostConstruct
    void init() {
        view.init(this);
    }

    public void setupCards(final List<CardComponent> cards) {
        view.clearGrid();
        cards.forEach(this::appendCard);
        setupEmptyState(cards.isEmpty());
    }

    public void setEmptyState(final HTMLElement emptyStateElement) {
        this.emptyStateElement = emptyStateElement;
    }

    private void setupEmptyState(final boolean isEmptyStateEnabled) {
        if (isEmptyStateEnabled && getEmptyStateElement().isPresent()) {
            view.appendCard(getEmptyStateElement().get());
        }
    }

    private Optional<HTMLElement> getEmptyStateElement() {
        return Optional.ofNullable(emptyStateElement);
    }

    private void appendCard(final CardComponent card) {
        view.appendCard(makeFrame(card).getElement());
    }

    private CardFrameComponent makeFrame(final CardComponent card) {
        final CardFrameComponent frame = frames.get();
        frame.initialize(card);
        return frame;
    }

    public HTMLElement getElement() {
        return view.getElement();
    }

    public interface View extends UberElemental<CardsGridComponent>,
                                  IsElement {

        void clearGrid();

        void appendCard(final HTMLElement cardElement);
    }
}
