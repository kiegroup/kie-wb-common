/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.kie.workbench.common.forms.editor.client.handler.formModel.container;

import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.IsWidget;
import org.gwtbootstrap3.client.ui.Radio;
import org.gwtbootstrap3.client.ui.gwt.FlowPanel;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Templated
public class FormModelCreationContainerViewImpl extends Composite implements FormModelCreationContainerView {

    private FormModelCreationContainer container;

    @DataField
    private Radio modelCreator = new Radio( "modelCreatorRadio" );

    @DataField
    private DivElement viewPanel = Document.get().createDivElement();

    @DataField
    private FlowPanel viewContainer = new FlowPanel();

    private boolean hasCreationView;

    @Override
    public void init( FormModelCreationContainer container ) {
        this.container = container;

        modelCreator.setText( container.getFormModelLabel() );

        IsWidget creationView = container.getCreationView();

        hasCreationView = creationView != null;

        if ( hasCreationView ) {
            viewContainer.add( creationView );
        }
    }

    @EventHandler( "modelCreator" )
    public void onClick( ClickEvent event ) {
        container.selectManager();
    }

    @Override
    public void hideCreationView() {
        viewPanel.getStyle().setDisplay( Style.Display.NONE );
    }

    @Override
    public void showCreationView() {
        if ( hasCreationView ) {
            viewPanel.getStyle().setDisplay( Style.Display.BLOCK );
        }
    }

    @Override
    public void select() {
        modelCreator.setValue( true );
    }
}
