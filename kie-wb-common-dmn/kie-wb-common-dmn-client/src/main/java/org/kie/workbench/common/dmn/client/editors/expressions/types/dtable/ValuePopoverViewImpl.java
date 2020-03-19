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

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.gwt.dom.client.BrowserEvents;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLInputElement;
import elemental2.dom.KeyboardEvent;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.EventListener;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.popover.AbstractPopoverViewImpl;
import org.uberfire.client.views.pfly.widgets.JQueryProducer;
import org.uberfire.client.views.pfly.widgets.Popover;

@Templated
@ApplicationScoped
public class ValuePopoverViewImpl extends AbstractPopoverViewImpl implements ValuePopoverView {

    static final String ESCAPE_KEY = "Escape";

    static final String ESC_KEY = "Esc";

    static final String ENTER_KEY = "Enter";

    @DataField("value-editor")
    private HTMLInputElement valueEditor;

    @DataField("value-label")
    private HTMLElement label;

    private final TranslationService translationService;

    private Presenter presenter;

    private String currentValue;

    private String previousValue;

    private Optional<Consumer> closedByKeyboardCallback;

    @Inject
    public ValuePopoverViewImpl(final Div popoverElement,
                                final Div popoverContentElement,
                                final JQueryProducer.JQuery<Popover> jQueryPopover,
                                final HTMLInputElement valueEditor,
                                @Named("span") final HTMLElement label,
                                final TranslationService translationService) {
        super(popoverElement,
              popoverContentElement,
              jQueryPopover);
        this.valueEditor = valueEditor;
        this.label = label;
        this.translationService = translationService;
        this.closedByKeyboardCallback = Optional.empty();
    }

    @PostConstruct
    public void setup() {
        label.textContent = translationService.getTranslation(DMNEditorConstants.ValuePopover_ValueLabel);
    }

    @Override
    public void setOnClosedByKeyboardCallback(final Consumer callback) {
        closedByKeyboardCallback = Optional.ofNullable(callback);
    }

    public void onClosedByKeyboard() {
        getClosedByKeyboardCallback().ifPresent(c -> c.accept(this));
    }

    Optional<Consumer> getClosedByKeyboardCallback() {
        return closedByKeyboardCallback;
    }

    @Override
    public void init(final Presenter presenter) {
        this.presenter = presenter;
        setKeyDownListeners();
    }

    void setKeyDownListeners() {
        popoverElement.addEventListener(BrowserEvents.KEYDOWN,
                                        getKeyDownEventListener(),
                                        false);
    }

    EventListener getKeyDownEventListener() {
        return (e) -> keyDownEventListener(e);
    }

    void keyDownEventListener(final Object event) {
        if (event instanceof KeyboardEvent) {
            final KeyboardEvent keyEvent = (KeyboardEvent) event;
            if (isEnterKeyPressed(keyEvent)) {
                hide(true);
                keyEvent.stopPropagation();
                onClosedByKeyboard();
            } else if (isEscapeKeyPressed(keyEvent)) {
                reset();
                hide(false);
                onClosedByKeyboard();
            }
        }
    }

    public void reset() {
        valueEditor.value = getPreviousValue();
        currentValue = getPreviousValue();
    }

    @Override
    public void hide() {
        hide(true);
    }

    public void hide(final boolean applyChanges) {
        if (isVisible()) {
            if (applyChanges) {
                currentValue = valueEditor.value;
                getPresenter().setValue(currentValue);
            } else {
                currentValue = getPreviousValue();
            }
            superHide();
        }
    }

    String getPreviousValue() {
        return previousValue;
    }

    Presenter getPresenter() {
        return presenter;
    }

    void superHide() {
        super.hide();
    }

    boolean isEscapeKeyPressed(final KeyboardEvent event) {
        return Objects.equals(event.key, ESC_KEY) || Objects.equals(event.key, ESCAPE_KEY);
    }

    boolean isEnterKeyPressed(final KeyboardEvent event) {
        return Objects.equals(event.key, ENTER_KEY);
    }

    @Override
    public void setValue(final String value) {
        valueEditor.value = value;
        currentValue = value;
        previousValue = value;
    }

    @Override
    public String getValue() {
        return currentValue;
    }
}