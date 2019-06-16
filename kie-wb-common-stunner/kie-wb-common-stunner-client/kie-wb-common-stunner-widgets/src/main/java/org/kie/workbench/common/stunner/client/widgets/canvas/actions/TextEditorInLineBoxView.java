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
import elemental2.dom.CSSStyleDeclaration;
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

import static elemental2.dom.CSSProperties.MaxWidthUnionType;
import static elemental2.dom.CSSProperties.WidthUnionType;


@Templated(value = "TextEditorInLineBox.html", stylesheet = "TextEditorInLineBox.css")
@InLineTextEditorBox
public class TextEditorInLineBoxView
        extends AbstractTextEditorBoxView
        implements TextEditorBoxView,
                   IsElement {

    protected double defaultWidth = 100.0;

    protected double defaultHeight = 20.0;

    protected String fontPosition;

    protected String fontAlignment;

    protected String orientation;

    protected double fontX = 0;

    protected double fontY = 0;

    @Inject
    @DataField
    protected HTMLDivElement nameField;

    @Inject
    public javax.enterprise.event.Event<OnNodeTitleChangeEvent> onNodeTitleChangeEvent;

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
        nameField.setAttribute("data-text",
                               translationService.getTranslation(StunnerWidgetsConstants.NameEditBoxWidgetViewImp_name));
        nameField.addEventListener("input", event -> onInputChange());
    }

    protected void onInputChange(){
        onResize();
        fireTitleChangeEvent();
    }

    protected void fireTitleChangeEvent() {
        onNodeTitleChangeEvent.fire(new OnNodeTitleChangeEvent());
    }

    protected void onResize() {
        double outer = ((HTMLDivElement) editNameBox).clientHeight;
        double inner = nameField.clientHeight;
        if (inner < outer) {
            nameField.style.borderLeft = "";
            nameField.style.borderRight = "";
        } else {
            nameField.style.borderLeft = "1px solid #0099d3";
            nameField.style.borderRight = "1px solid #0099d3";
        }
    }

    @Override
    public void init(TextEditorBoxView.Presenter presenter) {
        super.presenter = presenter;
    }

    @Override
    public void show(final String name) {
        nameField.textContent = "";
        nameField.innerHTML = "";
        nameField.innerHTML = name;
        setVisible();
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

    public void setWidth(double width) {
        if (!orientation.equals("VERTICAL")) {
            if (width < defaultWidth) {
                width = defaultWidth;
            }
            editNameBox.getStyle().setProperty("width", width + "px");
            setWidthUnionType(nameField.style, width);
            if(fontPosition.equals("INSIDE") && fontAlignment.equals("TOP") && orientation.equals("HORIZONTAL")) {
                setMaxWidthUnionType(nameField.style, width);
            }
        }
    }

    protected void setWidthUnionType(CSSStyleDeclaration style, double width) {
        style.width = WidthUnionType.of(width - 2 + "px");
    }

    protected void setMaxWidthUnionType(CSSStyleDeclaration style, double width) {
        style.maxWidth = MaxWidthUnionType.of(width - 2 + "px");
    }

    public void setHeight(double height) {
        if (fontPosition.equals("OUTSIDE") || fontAlignment.equals("TOP")) {
            height = defaultHeight;
            setStyleProperty(editNameBox, "height", height);
        } else {
            if (height < defaultHeight) {
                height = defaultHeight;
            }
        }
        setStyleProperty(editNameBox, "min-height", height);
        if (orientation.equals("VERTICAL")) {
            double width = 350;
            double maxWidth = (height *35)/100;
            setStyleProperty(editNameBox, "width", (width > maxWidth ? width : maxWidth));
            setStyleProperty(editNameBox, "max-width", (width > maxWidth ? width : maxWidth));

            setWidthUnionType(nameField.style, (width > maxWidth ? width : maxWidth) - 2);
            setMaxWidthUnionType(nameField.style, (width > maxWidth ? width : maxWidth) - 2);
        }
    }

    protected void setStyleProperty(Div div, String property, double height){
        div.getStyle().setProperty(property, height + "px");
    }

    public void setFontSize(double size) {
        nameField.style.fontSize = CSSProperties.FontSizeUnionType.of(size + "px");
    }

    public void setFontX(final double x) {
        this.fontX = x;
    }

    public void setFontY(final double y) {
        this.fontY = y;
    }

    public void setFontPosition(final String position) {
        this.fontPosition = position;
    }

    public void setFontAlignment(final String position) {
        this.fontAlignment = position;
    }

    public void setOrientation(final String orientation) {
        this.orientation = orientation;
    }

    public double getDisplayOffsetX() {
        if (fontPosition.equals("OUTSIDE")) {
            if (nameField.innerHTML != null && nameField.innerHTML.isEmpty()) {
                return -30;
            }
        }
        return 0;
    }

    public double getDisplayOffsetY() {
        return 0;
    }
}
