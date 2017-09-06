/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.widgets.configresource.client.widget.bound;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.cellview.client.TextHeader;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import org.appformer.project.datamodel.imports.Import;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Label;
import org.gwtbootstrap3.client.ui.constants.ButtonSize;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.gwt.ButtonCell;
import org.gwtbootstrap3.client.ui.gwt.CellTable;
import org.kie.workbench.common.widgets.configresource.client.resources.i18n.ImportConstants;
import org.uberfire.client.mvp.LockRequiredEvent;
import org.uberfire.mvp.ParameterizedCommand;

public class ImportsWidgetViewImpl
        extends Composite
        implements ImportsWidgetView {

    interface Binder
            extends UiBinder<Widget, ImportsWidgetViewImpl> {

    }

    private static Binder uiBinder = GWT.create(Binder.class);

    @UiField
    Button addImportButton;

    @UiField(provided = true)
    CellTable<Import> table = new CellTable<>();

    private AddImportPopup addImportPopup;

    private Event<LockRequiredEvent> lockRequired;

    private List<Import> externalFactTypes = new ArrayList<>();
    private ListDataProvider<Import> dataProvider = new ListDataProvider<>();

    private final Command addImportCommand = makeAddImportCommand();
    private final ParameterizedCommand<Import> removeImportCommand = makeRemoveImportCommand();

    private ImportsWidgetView.Presenter presenter;

    private final Comparator<Import> IMPORT_COMPARATOR = (o1, o2) -> {
        final boolean isO1internal = presenter.isInternalImport(o1);
        final boolean isO2internal = presenter.isInternalImport(o2);
        if (isO1internal) {
            if (isO2internal) {
                return o1.getType().compareTo(o2.getType());
            }
            return -1;
        }
        if (isO2internal) {
            return 1;
        }
        return o1.getType().compareTo(o2.getType());
    };

    private boolean isReadOnly = false;

    public ImportsWidgetViewImpl() {
        //CDI proxy
    }

    @Inject
    public ImportsWidgetViewImpl(final AddImportPopup addImportPopup,
                                 final Event<LockRequiredEvent> lockRequired) {
        this.addImportPopup = addImportPopup;
        this.lockRequired = lockRequired;

        setup();
        initWidget(uiBinder.createAndBindUi(this));

        //Disable until content is loaded
        addImportButton.setEnabled(false);
    }

    private void setup() {
        //Setup table
        table.setStriped(true);
        table.setCondensed(true);
        table.setBordered(true);
        table.setEmptyTableWidget(new Label(ImportConstants.INSTANCE.noImportsDefined()));

        //Columns
        final TextColumn<Import> importTypeColumn = new TextColumn<Import>() {

            @Override
            public String getValue(final Import importType) {
                return importType.getType();
            }
        };

        final ButtonCell deleteImportButton = new ButtonCell(IconType.TRASH,
                                                             ButtonType.DANGER,
                                                             ButtonSize.SMALL) {
            @Override
            public void render(final com.google.gwt.cell.client.Cell.Context context,
                               final SafeHtml data,
                               final SafeHtmlBuilder sb) {
                //Don't render a "Delete" button for "internal" Fact Types
                if (!presenter.isInternalImport(getDataProvider().getList().get(context.getIndex()))) {
                    super.render(context,
                                 data,
                                 sb);
                }
            }

            @Override
            public void onBrowserEvent(final Context context,
                                       final Element parent,
                                       final String value,
                                       final NativeEvent event,
                                       final ValueUpdater<String> valueUpdater) {
                //Don't act on cell interactions for "internal" Fact Types
                if (!presenter.isInternalImport(getDataProvider().getList().get(context.getIndex()))) {
                    super.onBrowserEvent(context,
                                         parent,
                                         value,
                                         event,
                                         valueUpdater);
                }
            }

            @Override
            protected void onEnterKeyDown(final Context context,
                                          final Element parent,
                                          final String value,
                                          final NativeEvent event,
                                          final ValueUpdater<String> valueUpdater) {
                //Don't act on cell interactions for "internal" Fact Types
                if (!presenter.isInternalImport(getDataProvider().getList().get(context.getIndex()))) {
                    super.onEnterKeyDown(context,
                                         parent,
                                         value,
                                         event,
                                         valueUpdater);
                }
            }
        };
        final Column<Import, String> deleteImportColumn = new Column<Import, String>(deleteImportButton) {
            @Override
            public String getValue(final Import importType) {
                return ImportConstants.INSTANCE.remove();
            }
        };
        deleteImportColumn.setFieldUpdater((index,
                                            importType,
                                            value) -> {
            if (isReadOnly) {
                return;
            }
            if (Window.confirm(ImportConstants.INSTANCE.promptForRemovalOfImport0(importType.getType()))) {
                getRemoveImportCommand().execute(importType);
            }
        });

        table.addColumn(importTypeColumn,
                        new TextHeader(ImportConstants.INSTANCE.importType()));
        table.addColumn(deleteImportColumn,
                        ImportConstants.INSTANCE.remove());

        //Link display
        getDataProvider().addDataDisplay(table);
    }

    @Override
    public void init(final ImportsWidgetView.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setContent(final List<Import> internalFactTypes,
                           final List<Import> externalFactTypes,
                           final List<Import> modelFactTypes,
                           final boolean isReadOnly) {
        this.externalFactTypes = externalFactTypes;
        this.getDataProvider().setList(new ArrayList<Import>() {{
            addAll(internalFactTypes);
            addAll(modelFactTypes);
        }});
        getDataProvider().getList().sort(IMPORT_COMPARATOR);
        this.addImportButton.setEnabled(!isReadOnly);
        this.isReadOnly = isReadOnly;
    }

    @UiHandler("addImportButton")
    public void onClickAddImportButton(final ClickEvent event) {
        addImportPopup.setContent(getAddImportCommand(),
                                  externalFactTypes);
        addImportPopup.show();
    }

    ParameterizedCommand<Import> makeRemoveImportCommand() {
        return (i) -> {
            getDataProvider().getList().remove(i);
            getDataProvider().getList().sort(IMPORT_COMPARATOR);
            presenter.onRemoveImport(i);
        };
    }

    Command getAddImportCommand() {
        return addImportCommand;
    }

    Command makeAddImportCommand() {
        return () -> {
            final Import importType = new Import(addImportPopup.getImportType());
            getDataProvider().getList().add(importType);
            getDataProvider().getList().sort(IMPORT_COMPARATOR);
            lockRequired.fire(new LockRequiredEvent());
            presenter.onAddImport(importType);
        };
    }

    ParameterizedCommand<Import> getRemoveImportCommand() {
        return removeImportCommand;
    }

    ListDataProvider<Import> getDataProvider() {
        return dataProvider;
    }
}
