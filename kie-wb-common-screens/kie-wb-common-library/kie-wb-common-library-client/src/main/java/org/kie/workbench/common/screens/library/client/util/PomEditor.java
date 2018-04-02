/*
 * Copyright (C) 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.library.client.util;

import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import elemental2.dom.DomGlobal;
import org.guvnor.common.services.project.client.type.POMResourceType;
import org.jboss.errai.common.client.api.Caller;
import org.kie.workbench.common.screens.defaulteditor.client.editor.KieTextEditorPresenter;
import org.kie.workbench.common.screens.defaulteditor.client.editor.KieTextEditorView;
import org.kie.workbench.common.services.shared.validation.ValidationService;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartTitleDecoration;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.promise.Promises;
import org.uberfire.ext.widgets.common.client.ace.AceEditorMode;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.menu.Menus;

@WorkbenchEditor(
        identifier = "PomEditor",
        supportedTypes = {POMResourceType.class},
        priority = 2)

public class PomEditor extends KieTextEditorPresenter {

    private final Promises promises;

    private final Caller<ValidationService> validationService;

    @Inject
    public PomEditor(final KieTextEditorView baseView,
                     final Promises promises,
                     final Caller<ValidationService> validationService) {

        super(baseView);
        this.promises = promises;
        this.validationService = validationService;
    }

    @OnStartup
    public void onStartup(final ObservablePath path,
                          final PlaceRequest place) {
        super.onStartup(path, place);
    }

    @Override
    protected void save(final String commitMessage) {
        promises.promisify(validationService, s -> {
            Path path = getPathSupplier().get();Ω
            String content = getContentSupplier().get();
            return s.validateForSave(path, content);
        }).then(errors -> {

            if (errors.isEmpty()) {
                super.save(commitMessage);
            } else {
                DomGlobal.console.info("Error!");
            }

            return promises.resolve();
        });
    }

    @WorkbenchPartTitle
    public String getTitleText() {
        return super.getTitleText();
    }

    @WorkbenchPartTitleDecoration
    public IsWidget getTitle() {
        return super.getTitle();
    }

    @WorkbenchPartView
    public IsWidget asWidget() {
        return super.getWidget();
    }

    @Override
    protected Command onValidate() {
        return super.onValidate();
    }

    @WorkbenchMenu
    public Menus getMenus() {
        return menus;
    }

    @Override
    public AceEditorMode getAceEditorMode() {
        return AceEditorMode.XML;
    }
}
