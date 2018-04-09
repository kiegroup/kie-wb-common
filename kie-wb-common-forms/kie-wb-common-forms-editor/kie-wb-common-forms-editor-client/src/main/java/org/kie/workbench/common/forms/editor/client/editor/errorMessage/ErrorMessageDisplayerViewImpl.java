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

package org.kie.workbench.common.forms.editor.client.editor.errorMessage;

import javax.inject.Inject;

import com.google.gwt.event.dom.client.ClickEvent;
import org.jboss.errai.common.client.dom.Anchor;
import org.jboss.errai.common.client.dom.Button;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Templated
public class ErrorMessageDisplayerViewImpl implements ErrorMessageDisplayerView,
                                                      IsElement{

    @Inject
    @DataField
    private Button closeButton;

    @Inject
    @DataField
    private Div errorContainer;

    @Inject
    @DataField
    private Span errorMessageContainer;

    @Inject
    @DataField
    private Anchor showMoreAnchor;

    private Presenter presenter;

    @Override
    public void init(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setMessage(String message) {
        errorMessageContainer.setTextContent(message);
        getElement().setHidden(false);
    }

    @Override
    public void hide() {
        getElement().setHidden(true);
    }

    @Override
    public void displayShowMoreAnchor(boolean display) {
        showMoreAnchor.setHidden(!display);
    }

    @Override
    public void setShowMoreLabel(String label) {
        showMoreAnchor.setTitle(label);
        showMoreAnchor.setTextContent(label);
    }

    @EventHandler("showMoreAnchor")
    public void onShowMore(ClickEvent event) {
        presenter.notifyShowMorePressed();
    }

    @EventHandler("closeButton")
    public void onCloseButton(ClickEvent event) {
        presenter.hide();
    }
}
