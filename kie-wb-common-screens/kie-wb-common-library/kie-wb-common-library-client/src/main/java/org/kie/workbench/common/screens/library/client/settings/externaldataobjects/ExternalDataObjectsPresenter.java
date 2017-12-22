/*
 * Copyright (C) 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.library.client.settings.externaldataobjects;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.soup.project.datamodel.imports.Import;
import org.kie.soup.project.datamodel.imports.Imports;
import org.kie.workbench.common.screens.library.client.settings.SettingsPresenter;
import org.kie.workbench.common.screens.projecteditor.model.ProjectScreenModel;
import org.kie.workbench.common.widgets.configresource.client.widget.unbound.AddImportPopup;
import org.uberfire.ext.widgets.common.client.common.HasBusyIndicator;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

public class ExternalDataObjectsPresenter implements SettingsPresenter.Section {

    private final View view;
    private final ManagedInstance<ExternalDataObjectsItemPresenter> itemPresenters;
    private final AddImportPopup addImportPopup;

    private Imports imports;
    private Map<Import, ExternalDataObjectsItemPresenter> presentersByImports;

    public interface View extends SettingsPresenter.View.Section<ExternalDataObjectsPresenter> {

        void setItems(final List<ExternalDataObjectsItemPresenter.View> itemViews);

        void remove(final ExternalDataObjectsItemPresenter.View view);

        void add(final ExternalDataObjectsItemPresenter.View view);
    }

    @Inject
    public ExternalDataObjectsPresenter(final View view,
                                        final AddImportPopup addImportPopup,
                                        final ManagedInstance<ExternalDataObjectsItemPresenter> itemPresenters) {

        this.view = view;
        this.itemPresenters = itemPresenters;
        this.addImportPopup = addImportPopup;
    }

    @Override
    public void setup(final HasBusyIndicator container,
                      final ProjectScreenModel model) {

        view.init(this);

        imports = model.getProjectImports().getImports();

        presentersByImports = imports.getImports()
                .stream()
                .collect(toMap(identity(), this::newItemPresenter));

        view.setItems(presentersByImports.values().stream()
                              .map(ExternalDataObjectsItemPresenter::getView)
                              .collect(toList()));
    }

    public void openAddPopup() {
        //FIXME: create new popup?
        addImportPopup.show();
        addImportPopup.setCommand(() -> addImport(addImportPopup.getImportType()));
    }

    private void removeImport(final Import import_) {
        imports.removeImport(import_);
        view.remove(presentersByImports.get(import_).getView());
    }

    private void addImport(final String typeName) {
        final Import newImport = new Import(typeName);

        imports.addImport(newImport);

        final ExternalDataObjectsItemPresenter presenter = newItemPresenter(newImport);
        presentersByImports.put(newImport, presenter);
        view.add(presenter.getView());
    }

    private ExternalDataObjectsItemPresenter newItemPresenter(final Import import_) {
        return itemPresenters.get().setup(import_, this::removeImport);
    }

    @Override
    public SettingsPresenter.View.Section getView() {
        return view;
    }
}
