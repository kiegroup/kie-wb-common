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
import java.util.List;
import java.util.function.Supplier;

import javax.enterprise.event.Event;
import javax.inject.Inject;

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
import org.kie.workbench.common.screens.library.client.settings.persistence.properties.NewPersistableDataObjectPopupPresenter;
import org.kie.workbench.common.screens.library.client.settings.persistence.properties.NewPropertyPopupPresenter;
import org.kie.workbench.common.screens.library.client.settings.persistence.properties.PropertiesItemPresenter;
import org.kie.workbench.common.screens.projecteditor.model.ProjectScreenModel;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.workbench.events.NotificationEvent;

import static java.util.stream.Collectors.toList;
import static org.kie.workbench.common.screens.library.client.settings.Promises.resolve;
import static org.uberfire.workbench.events.NotificationEvent.NotificationType.WARNING;

public class PersistencePresenter implements SettingsPresenter.Section {

    private final View view;
    private final ProjectContext projectContext;
    private final Event<NotificationEvent> notificationEvent;
    private final Event<SettingsSectionChange> settingsSectionChangeEvent;
    private final ManagedInstance<ObservablePath> observablePaths;
    private final ManagedInstance<PropertiesItemPresenter> propertiesItemPresenters;
    private final ManagedInstance<PersistableDataObjectsItemPresenter> persistableDataObjectsItemPresenters;
    private final NewPropertyPopupPresenter newPropertyPopupPresenter;
    private final NewPersistableDataObjectPopupPresenter newPersistableDataObjectPopupPresenter;
    private final Caller<PersistenceDescriptorEditorService> editorService;
    private final Caller<PersistenceDescriptorService> descriptorService;
    private final Caller<DataModelerService> dataModelerService;

    private ObservablePath pathToPersistenceXml;
    private PersistenceDescriptorEditorContent persistenceDescriptorEditorContent;
    private ObservablePath.OnConcurrentUpdateEvent concurrentPersistenceXmlUpdateInfo;

    public interface View extends SettingsPresenter.View.Section<PersistencePresenter> {

        void setPersistenceUnit(String persistenceUnit);

        void setPersistenceProvider(String persistenceProvider);

        void setDataSource(String dataSource);

        void setPropertiesItems(final List<PropertiesItemPresenter.View> items);

        void add(PropertiesItemPresenter.View propertyItem);

        void setPersistableDataObjectsItems(final List<PersistableDataObjectsItemPresenter.View> items);

        void add(PersistableDataObjectsItemPresenter.View persistableDataObjectItem);

        void remove(PersistableDataObjectsItemPresenter.View view);

        void remove(PropertiesItemPresenter.View view);

        String getConcurrentUpdateMessage();
    }

    @Inject
    public PersistencePresenter(final PersistencePresenter.View view,
                                final ProjectContext projectContext,
                                final Event<NotificationEvent> notificationEvent,
                                final Event<SettingsSectionChange> settingsSectionChangeEvent,
                                final ManagedInstance<ObservablePath> observablePaths,
                                final ManagedInstance<PropertiesItemPresenter> propertiesItemPresenters,
                                final ManagedInstance<PersistableDataObjectsItemPresenter> persistableDataObjectsItemPresenters,
                                final NewPropertyPopupPresenter newPropertyPopupPresenter,
                                final NewPersistableDataObjectPopupPresenter newPersistableDataObjectPopupPresenter,
                                final Caller<PersistenceDescriptorEditorService> editorService,
                                final Caller<PersistenceDescriptorService> descriptorService,
                                final Caller<DataModelerService> dataModelerService) {

        this.view = view;
        this.projectContext = projectContext;
        this.notificationEvent = notificationEvent;
        this.settingsSectionChangeEvent = settingsSectionChangeEvent;
        this.observablePaths = observablePaths;
        this.propertiesItemPresenters = propertiesItemPresenters;
        this.persistableDataObjectsItemPresenters = persistableDataObjectsItemPresenters;
        this.newPropertyPopupPresenter = newPropertyPopupPresenter;
        this.newPersistableDataObjectPopupPresenter = newPersistableDataObjectPopupPresenter;
        this.editorService = editorService;
        this.descriptorService = descriptorService;
        this.dataModelerService = dataModelerService;
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
        newPersistableDataObjectPopupPresenter.setup(this);

        return Promises.promisify(editorService, s -> s.loadContent(pathToPersistenceXml, true)).then(m -> {
            persistenceDescriptorEditorContent = m;

            view.setPersistenceUnit(getPersistenceUnitModel().getName());
            view.setPersistenceProvider(getPersistenceUnitModel().getProvider());
            view.setDataSource(getPersistenceUnitModel().getJtaDataSource());

            view.setPropertiesItems(
                    getPersistenceUnitModel().getProperties()
                            .stream()
                            .map(property -> propertiesItemPresenters.get().setup(property, this))
                            .map(PropertiesItemPresenter::getView)
                            .collect(toList()));

            view.setPersistableDataObjectsItems(
                    getPersistenceUnitModel().getClasses()
                            .stream()
                            .map(className -> persistableDataObjectsItemPresenters.get().setup(className, this))
                            .map(PersistableDataObjectsItemPresenter::getView)
                            .collect(toList()));

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
        getPersistenceUnitModel().getClasses().add(className);
        view.add(persistableDataObjectsItemPresenters.get().setup(className, this).getView());
        fireChangeEvent(settingsSectionChangeEvent);
    }

    public void add(final Property property) {
        getPersistenceUnitModel().addProperty(property);
        view.add(propertiesItemPresenters.get().setup(property, this).getView());
        fireChangeEvent(settingsSectionChangeEvent);
    }

    public void addAllProjectsPersistableDataObjects() {
        Promises.promisify(dataModelerService, s -> s.findPersistableClasses(pathToPersistenceXml)).then(classes -> {
            classes.stream()
                    .filter(c -> !getPersistenceUnitModel().getClasses().contains(c))
                    .forEach(this::add);

            return resolve();
        });
    }

    public void remove(final PersistableDataObjectsItemPresenter itemPresenter) {
        getPersistenceUnitModel().getClasses().remove(itemPresenter.getClassName());
        view.remove(itemPresenter.getView());
        fireChangeEvent(settingsSectionChangeEvent);
    }

    public void remove(final PropertiesItemPresenter itemPresenter) {
        getPersistenceUnitModel().getProperties().remove(itemPresenter.getProperty());
        view.remove(itemPresenter.getView());
        fireChangeEvent(settingsSectionChangeEvent);
    }

    public void setDataSource(final String dataSource) {
        getPersistenceUnitModel().setJtaDataSource(dataSource);
        fireChangeEvent(settingsSectionChangeEvent);
    }

    public void setPersistenceUnit(final String persistenceUnit) {
        getPersistenceUnitModel().setName(persistenceUnit);
        fireChangeEvent(settingsSectionChangeEvent);
    }

    public void setPersistenceProvider(final String persistenceProvider) {
        getPersistenceUnitModel().setProvider(persistenceProvider);
        fireChangeEvent(settingsSectionChangeEvent);
    }

    private PersistenceUnitModel getPersistenceUnitModel() {
        return this.persistenceDescriptorEditorContent.getDescriptorModel().getPersistenceUnit();
    }

    public void showNewPropertyPopup() {
        newPropertyPopupPresenter.show();
    }

    public void showNewPersistableDataObjectPopup() {
        newPersistableDataObjectPopupPresenter.show();
    }

    @Override
    public int currentHashCode() {
        return getPersistenceUnitModel().hashCode();
    }

    @Override
    public SettingsPresenter.View.Section getView() {
        return view;
    }
}
