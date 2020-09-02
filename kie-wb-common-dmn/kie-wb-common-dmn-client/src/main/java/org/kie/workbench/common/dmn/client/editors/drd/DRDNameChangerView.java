/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.editors.drd;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.user.client.DOM;
import elemental2.dom.HTMLAnchorElement;
import org.gwtbootstrap3.client.ui.html.Span;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.dmn.api.definition.model.DMNDiagramElement;
import org.kie.workbench.common.dmn.client.docks.navigator.drds.DMNDiagramSelected;
import org.kie.workbench.common.dmn.client.docks.navigator.drds.DMNDiagramsSession;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionPresenter;

import static com.google.gwt.dom.client.Style.Display.BLOCK;
import static com.google.gwt.dom.client.Style.Display.NONE;

@Templated
@Dependent
public class DRDNameChangerView implements DRDNameChanger {

    private final DMNDiagramsSession dmnDiagramsSession;
    private final Event<DMNDiagramSelected> selectedEvent;
    private SessionPresenter.View sessionPresenterView;

    @DataField("viewMode")
    private final DivElement viewMode;

    @DataField("editMode")
    private final DivElement editMode;

    @DataField("returnToDRG")
    private final HTMLAnchorElement returnToDRG;

    @DataField("drdName")
    private final Span drdName;

    @DataField("drdNameInput")
    private final InputElement drdNameInput;

    @Inject
    public DRDNameChangerView(final DMNDiagramsSession dmnDiagramsSession, final Event<DMNDiagramSelected> selectedEvent, final DivElement viewMode, final DivElement editMode, final HTMLAnchorElement returnToDRG, final Span drdName, final InputElement drdNameInput) {
        this.dmnDiagramsSession = dmnDiagramsSession;
        this.selectedEvent = selectedEvent;
        this.viewMode = viewMode;
        this.editMode = editMode;
        this.returnToDRG = returnToDRG;
        this.drdName = drdName;
        this.drdNameInput = drdNameInput;
    }

    @Override
    public void setSessionPresenterView(SessionPresenter.View sessionPresenterView) {
        this.sessionPresenterView = sessionPresenterView;
    }

    void onSettingCurrentDMNDiagramElement(final @Observes DMNDiagramSelected selected) {
        if (dmnDiagramsSession.isGlobalGraph()) {
            DOM.getElementById("drd-name-changer").getStyle().setDisplay(NONE);
        } else {
            this.drdName.setText(selected.getDiagramElement().getName().getValue());
            editMode.getStyle().setDisplay(NONE);
            viewMode.getStyle().setDisplay(BLOCK);
            DOM.getElementById("drd-name-changer").getStyle().setDisplay(BLOCK);
            sessionPresenterView.onResize();
        }
    }

    @EventHandler("returnToDRG")
    void onClickReturnToDRG(final ClickEvent event) {
        DOM.getElementById("drd-name-changer").getStyle().setDisplay(NONE);
        selectedEvent.fire(new DMNDiagramSelected(dmnDiagramsSession.getDRGDMNDiagramElement()));
    }

    @EventHandler("viewMode")
    void enableEdit(final ClickEvent event) {
        drdNameInput.setValue(drdName.getText());
        viewMode.getStyle().setDisplay(NONE);
        editMode.getStyle().setDisplay(BLOCK);
        drdNameInput.focus();
    }

    @EventHandler("drdNameInput")
    void onInputTextKeyPress(final KeyDownEvent event) {
        if (event.getNativeEvent().getKeyCode() == 13) {
            saveForTheCurrentDiagram();
        }
    }

    @EventHandler("drdNameInput")
    void onInputTextBlur(final BlurEvent event) {
        saveForTheCurrentDiagram();
    }

    void saveForTheCurrentDiagram() {
        dmnDiagramsSession.getCurrentDMNDiagramElement().ifPresent(this::performSave);
    }

    private void performSave(final DMNDiagramElement dmnDiagramElement) {
        dmnDiagramElement.getName().setValue(drdNameInput.getValue());
        selectedEvent.fire(new DMNDiagramSelected(dmnDiagramElement));
    }

}
