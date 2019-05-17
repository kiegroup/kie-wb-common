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

package org.kie.workbench.common.stunner.client.widgets.canvas.actions;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.Event;
import elemental2.dom.CSSProperties;
import elemental2.dom.HTMLDivElement;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.SinkNative;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.stunner.client.widgets.resources.i18n.StunnerWidgetsConstants;
import org.kie.workbench.common.stunner.core.client.canvas.controls.actions.InLineTextEditorBox;
import org.uberfire.mvp.Command;

@Templated(value = "TextEditorInLineBox.html", stylesheet = "TextEditorInLineBox.css")
@InLineTextEditorBox
public class TextEditorInLineBoxView
        extends AbstractTextEditorBoxView
        implements TextEditorBoxView,
                   IsElement {

    private double defaultWidth = 100.0;

    private double defaultHeight = 100.0;

    @Inject
    @DataField
    private HTMLDivElement nameField;

    @Inject
    public TextEditorInLineBoxView(final TranslationService translationService) {
        super();
        this.translationService = translationService;
    }

    TextEditorInLineBoxView(final TranslationService translationService,
                            final Div editNameBox,
                            final HTMLDivElement nameField,
                            final Command showCommand,
                            final Command hideCommand,
                            final HTMLElement closeButton,
                            final HTMLElement saveButton) {
        super(showCommand, hideCommand, closeButton, saveButton);
        this.translationService = translationService;
        this.nameField = nameField;
        super.editNameBox = editNameBox;
    }

    @PostConstruct
    public void initialize() {
        super.initialize();
        nameField.setAttribute("placeHolder",
                               translationService.getTranslation(StunnerWidgetsConstants.NameEditBoxWidgetViewImp_name));
    }

    @Override
    public void init(TextEditorBoxView.Presenter presenter) {
        super.presenter = presenter;
    }

    @Override
    public void show(final String name) {
        nameField.innerHTML = name.replaceAll("\\n", "<br>").replaceAll("\\s", "&nbsp;");
        setVisible();
        nameField.focus();
    }

    @EventHandler("editNameBox")
    @SinkNative(Event.ONKEYDOWN)
    public void editNameBoxEsc(Event event) {
        switch (event.getTypeInt()) {
            case Event.ONKEYDOWN:
                if (event.getKeyCode() == KeyCodes.KEY_ESCAPE) {
                    presenter.onClose();
                }
                break;
        }
    }

    @EventHandler("nameField")
    @SinkNative(Event.ONCHANGE | Event.ONKEYPRESS | Event.ONKEYDOWN)
    public void onChangeName(Event event) {
        switch (event.getTypeInt()) {
            case Event.ONCHANGE:
                presenter.onChangeName(getNameFieldValue());
                break;
            case Event.ONKEYPRESS:
                presenter.onKeyPress(event.getKeyCode(),
                                     event.getShiftKey(),
                                     getNameFieldValue());
                break;
            case Event.ONKEYDOWN:
                //Defer processing of KeyDownEvent until after KeyPress has been processed as we write the value to the Presenter from the TextArea.
                scheduleDeferredCommand(() -> presenter.onKeyDown(event.getKeyCode(), getNameFieldValue()));
        }
    }

    String getNameFieldValue() {
        return nameField.innerHTML.replaceAll("<br>", "\n").replaceAll("&nbsp;", " ");
    }

    public void setWidth(final double width) {
        if (width > defaultWidth) {
            editNameBox.getStyle().setProperty("width", width + "px");
            nameField.style.width = CSSProperties.WidthUnionType.of(width + "px");
        }
    }

    public void setHeight(final double height) {
        if (height > defaultHeight) {
            editNameBox.getStyle().setProperty("min-height", height + "px");
        }
    }

    public void setFontSize(double size) {
        nameField.style.fontSize = CSSProperties.FontSizeUnionType.of(size + "px");
    }
}
