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

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.common.client.api.Assert;
import org.kie.workbench.common.forms.editor.client.handler.formModel.FormModelCreationViewManager;
import org.kie.workbench.common.forms.editor.client.handler.formModel.SelectModelCreatorManagerCallback;
import org.uberfire.backend.vfs.Path;

@Dependent
public class FormModelCreationContainer implements IsWidget {

    private FormModelCreationContainerView view;

    private SelectModelCreatorManagerCallback callback;

    private FormModelCreationViewManager creationViewManager;

    @Inject
    public FormModelCreationContainer( FormModelCreationContainerView view ) {
        this.view = view;
    }

    public void setup( FormModelCreationViewManager creationViewManager, SelectModelCreatorManagerCallback callback ) {
        Assert.notNull( "Manager cannot be null", creationViewManager );
        Assert.notNull( "Callback cannot be null", callback );

        this.creationViewManager = creationViewManager;
        this.callback = callback;

        view.init( this );
    }

    public FormModelCreationViewManager getCreationViewManager() {
        return creationViewManager;
    }

    public void selectManager() {
        view.select();
        callback.selectContainerCallback( this );
    }

    public void showCreationView() {
        view.showCreationView();
    }

    public void hideCreationView() {
        creationViewManager.reset();
        view.hideCreationView();
    }

    public boolean isValid() {
        return creationViewManager.isValid();
    }

    public IsWidget getCreationView() {
        return creationViewManager.getView();
    }


    public String getFormModelLabel() {
        return creationViewManager.getLabel();
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    public void initData( Path projectPath ) {
        creationViewManager.init( projectPath );
    }
}
