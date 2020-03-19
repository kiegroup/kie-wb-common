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

package org.kie.workbench.common.dmn.client.editors.expressions.types.dtable;

import java.util.Optional;
import java.util.function.Consumer;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.dmn.client.editors.types.CanBeClosedByKeyboard;
import org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants;

@ApplicationScoped
public class ValuePopoverImpl implements ValuePopoverView.Presenter {

    private ValuePopoverView view;
    private TranslationService translationService;
    private Optional<HasValueSelectorControl> binding = Optional.empty();

    public ValuePopoverImpl() {
        //CDI proxy
    }

    @Inject
    public ValuePopoverImpl(final ValuePopoverView view,
                            final TranslationService translationService) {
        this.view = view;
        this.translationService = translationService;

        view.init(this);
    }

    public Optional<HasValueSelectorControl> getBinding() {
        return binding;
    }

    @Override
    public void show() {
        getBinding().ifPresent(b -> {
            view.show(Optional.ofNullable(getPopoverTitle()));
            view.setValue(b.getValue());
        });
    }

    @Override
    public void hide() {
        refresh();
        view.hide();
    }

    @Override
    public void refresh() {
        getBinding().ifPresent(b -> b.setValue(view.getValue()));
    }

    @Override
    public void setValue(final String value) {
        getBinding().ifPresent(b -> b.setValue(value));
    }

    @Override
    public String getPopoverTitle() {
        return translationService.getTranslation(DMNEditorConstants.DecisionTableEditor_SelectRuleAnnotationName);
    }

    @Override
    public void bind(final HasValueSelectorControl hasValueSelectorControl,
                     final int uiRowIndex,
                     final int uiColumnIndex) {
        binding = Optional.ofNullable(hasValueSelectorControl);
    }

    @Override
    public HTMLElement getElement() {
        return view.getElement();
    }

    @Override
    public void setOnClosedByKeyboardCallback(final Consumer<CanBeClosedByKeyboard> callback) {
        view.setOnClosedByKeyboardCallback(callback);
    }
}