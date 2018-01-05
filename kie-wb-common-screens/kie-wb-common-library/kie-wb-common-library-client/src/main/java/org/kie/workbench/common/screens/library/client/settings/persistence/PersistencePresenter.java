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

package org.kie.workbench.common.screens.library.client.settings.persistence;

import java.util.HashMap;
import java.util.function.Supplier;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import elemental2.dom.Element;
import elemental2.promise.Promise;
import org.guvnor.common.services.project.context.ProjectContext;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.screens.datamodeller.model.persistence.PersistenceDescriptorEditorContent;
import org.kie.workbench.common.screens.datamodeller.model.persistence.PersistenceUnitModel;
import org.kie.workbench.common.screens.datamodeller.model.persistence.Property;
import org.kie.workbench.common.screens.datamodeller.service.DataModelerService;
import org.kie.workbench.common.screens.datamodeller.service.PersistenceDescriptorEditorService;
import org.kie.workbench.common.screens.datamodeller.service.PersistenceDescriptorService;
import org.kie.workbench.common.screens.library.client.settings.Promises;
import org.kie.workbench.common.screens.library.client.settings.SettingsPresenter;
import org.kie.workbench.common.screens.library.client.settings.SettingsSectionChange;
import org.kie.workbench.common.screens.library.client.settings.persistence.persistabledataobjects.PersistableDataObjectsItemPresenter;
import org.kie.workbench.common.screens.library.client.settings.persistence.properties.NewPropertyPopupPresenter;
import org.kie.workbench.common.screens.library.client.settings.persistence.properties.PropertiesItemPresenter;
import org.kie.workbench.common.screens.library.client.settings.util.ListPresenter;
import org.kie.workbench.common.screens.projecteditor.model.ProjectScreenModel;
import org.kie.workbench.common.widgets.client.popups.text.TextBoxFormPopup;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.workbench.events.NotificationEvent;

import static org.kie.workbench.common.screens.library.client.settings.Promises.resolve;
import static org.uberfire.workbench.events.NotificationEvent.NotificationType.WARNING;

public class PersistencePresenter extends SettingsPresenter.Section {

    private final View view;
    private final ProjectContext projectContext;
    private final Event<NotificationEvent> notificationEvent;
    private final ManagedInstance<ObservablePath> observablePaths;
    private final NewPropertyPopupPresenter newPropertyPopupPresenter;
    private final TextBoxFormPopup textBoxFormPopup;
    private final Caller<PersistenceDescriptorEditorService> editorService;
    private final Caller<PersistenceDescriptorService> descriptorService;
    private final Caller<DataModelerService> dataModelerService;

    private final PropertiesListPresenter propertiesItemPresenters;
    private final PersistableDataObjectsListPresenter persistableDataObjectsItemPresenters;

    private ObservablePath pathToPersistenceXml;
    private PersistenceDescriptorEditorContent persistenceDescriptorEditorContent;
    private ObservablePath.OnConcurrentUpdateEvent concurrentPersistenceXmlUpdateInfo;

    public interface View extends SettingsPresenter.View.Section<PersistencePresenter> {

        void setPersistenceUnit(String persistenceUnit);

        void setPersistenceProvider(String persistenceProvider);

        void setDataSource(String dataSource);

        String getConcurrentUpdateMessage();

        Element getPropertiesTable();

        Element getPersistableDataObjectsTable();
    }

    @Inject
    public PersistencePresenter(final View view,
                                final ProjectContext projectContext,
                                final Event<NotificationEvent> notificationEvent,
                                final Event<SettingsSectionChange> settingsSectionChangeEvent,
                                final ManagedInstance<ObservablePath> observablePaths,
                                final NewPropertyPopupPresenter newPropertyPopupPresenter,
                                final TextBoxFormPopup textBoxFormPopup,
                                final Caller<PersistenceDescriptorEditorService> editorService,
                                final Caller<PersistenceDescriptorService> descriptorService,
                                final Caller<DataModelerService> dataModelerService,
                                final PropertiesListPresenter propertiesItemPresenters,
                                final PersistableDataObjectsListPresenter persistableDataObjectsItemPresenters) {

        super(settingsSectionChangeEvent);
        this.view = view;
        this.projectContext = projectContext;
        this.notificationEvent = notificationEvent;
        this.observablePaths = observablePaths;
        this.newPropertyPopupPresenter = newPropertyPopupPresenter;
        this.textBoxFormPopup = textBoxFormPopup;
        this.editorService = editorService;
        this.descriptorService = descriptorService;
        this.dataModelerService = dataModelerService;
        this.propertiesItemPresenters = propertiesItemPresenters;
        this.persistableDataObjectsItemPresenters = persistableDataObjectsItemPresenters;
    }

    @Override
    public Promise<Void> setup(final ProjectScreenModel model) {
        return setup();
    }

    private Promise<Void> setup() {
        view.init(this);

        final String persistenceXmlUri = projectContext.getActiveProject()
                .getRootPath().toURI() + "/src/main/resources/META-INF/persistence.xml";

        pathToPersistenceXml = observablePaths.get().wrap(PathFactory.newPath(
                "persistence.xml",
                persistenceXmlUri,
                new HashMap<String, Object>() {{
                    put(PathFactory.VERSION_PROPERTY, true);
                }}));

        pathToPersistenceXml.onConcurrentUpdate(info -> concurrentPersistenceXmlUpdateInfo = info);

        newPropertyPopupPresenter.setup(this);

        return Promises.promisify(editorService, s -> s.loadContent(pathToPersistenceXml, true)).then(m -> {
            persistenceDescriptorEditorContent = m;

            view.setPersistenceUnit(getPersistenceUnitModel().getName());
            view.setPersistenceProvider(getPersistenceUnitModel().getProvider());
            view.setDataSource(getPersistenceUnitModel().getJtaDataSource());

            propertiesItemPresenters.setup(
                    view.getPropertiesTable(),
                    getPersistenceUnitModel().getProperties(),
                    (property, presenter) -> presenter.setup(property, this));

            persistableDataObjectsItemPresenters.setup(
                    view.getPersistableDataObjectsTable(),
                    getPersistenceUnitModel().getClasses(),
                    (className, presenter) -> presenter.setup(className, this));

            return resolve();
        });
    }

    @Override
    public Promise<Void> save(final String comment,
                              final Supplier<Promise<Void>> chain) {

        if (concurrentPersistenceXmlUpdateInfo == null) {
            return save(comment);
        } else {
            notificationEvent.fire(new NotificationEvent(view.getConcurrentUpdateMessage(), WARNING));
            return setup();
        }
    }

    private Promise<Void> save(final String comment) {
        return Promises.promisify(editorService,
                                  s -> s.save(pathToPersistenceXml,
                                              persistenceDescriptorEditorContent,
                                              persistenceDescriptorEditorContent.getOverview().getMetadata(),
                                              comment)).then(i -> resolve());
    }

    public void add(final String className) {
        persistableDataObjectsItemPresenters.add(className);
        fireChangeEvent();
    }

    public void add(final Property property) {
        propertiesItemPresenters.add(property);
        fireChangeEvent();
    }

    public void addAllProjectsPersistableDataObjects() {
        Promises.promisify(dataModelerService, s -> s.findPersistableClasses(pathToPersistenceXml)).then(classes -> {
            classes.stream()
                    .filter(clazz -> !getPersistenceUnitModel().getClasses().contains(clazz))
                    .forEach(this::add);

            return resolve();
        });
    }

    public void setDataSource(final String dataSource) {
        getPersistenceUnitModel().setJtaDataSource(dataSource);
        fireChangeEvent();
    }

    public void setPersistenceUnit(final String persistenceUnit) {
        getPersistenceUnitModel().setName(persistenceUnit);
        fireChangeEvent();
    }

    public void setPersistenceProvider(final String persistenceProvider) {
        getPersistenceUnitModel().setProvider(persistenceProvider);
        fireChangeEvent();
    }

    private PersistenceUnitModel getPersistenceUnitModel() {
        return this.persistenceDescriptorEditorContent.getDescriptorModel().getPersistenceUnit();
    }

    public void showNewPropertyPopup() {
        newPropertyPopupPresenter.show();
    }

    public void showNewPersistableDataObjectPopup() {
        textBoxFormPopup.show(className -> {
            add(className);
            fireChangeEvent();
        });
    }

    @Override
    public int currentHashCode() {
        return getPersistenceUnitModel().hashCode();
    }

    @Override
    public SettingsPresenter.View.Section getView() {
        return view;
    }

    @Dependent
    public static class PersistableDataObjectsListPresenter extends ListPresenter<String, PersistableDataObjectsItemPresenter> {

        @Inject
        public PersistableDataObjectsListPresenter(final ManagedInstance<PersistableDataObjectsItemPresenter> itemPresenters) {
            super(itemPresenters);
        }
    }

    @Dependent
    public static class PropertiesListPresenter extends ListPresenter<Property, PropertiesItemPresenter> {

        @Inject
        public PropertiesListPresenter(final ManagedInstance<PropertiesItemPresenter> itemPresenters) {
            super(itemPresenters);
        }
    }
}
