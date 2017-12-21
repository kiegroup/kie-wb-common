/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.library.client.settings;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import elemental2.dom.DomGlobal;
import elemental2.promise.Promise;
import org.guvnor.common.services.project.service.DeploymentMode;
import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.kie.workbench.common.screens.library.client.perspective.LibraryPerspective;
import org.kie.workbench.common.screens.library.client.settings.dependencies.DependenciesPresenter;
import org.kie.workbench.common.screens.library.client.settings.deployments.DeploymentsPresenter;
import org.kie.workbench.common.screens.library.client.settings.externaldataobjects.ExternalDataObjectsPresenter;
import org.kie.workbench.common.screens.library.client.settings.generalsettings.GeneralSettingsPresenter;
import org.kie.workbench.common.screens.library.client.settings.knowledgebases.KnowledgeBasesPresenter;
import org.kie.workbench.common.screens.library.client.settings.persistence.PersistencePresenter;
import org.kie.workbench.common.screens.library.client.settings.validation.ValidationPresenter;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberElemental;
import org.uberfire.ext.editor.commons.client.file.popups.SavePopUpPresenter;
import org.uberfire.ext.widgets.common.client.common.HasBusyIndicator;
import org.uberfire.workbench.events.NotificationEvent;

import static org.uberfire.workbench.events.NotificationEvent.NotificationType.SUCCESS;

@WorkbenchScreen(identifier = "project-settings",
        owningPerspective = LibraryPerspective.class)
public class SettingsPresenter {

    public interface View extends UberElemental<SettingsPresenter>,
                                  HasBusyIndicator {

        void showBusyIndicator();

        void setContent(final Section contentView);

        String getSaveSuccessMessage();

        String getSavingMessage();

        interface Section<T> extends UberElemental<T>,
                                     IsElement {

        }
    }

    private final View view;

    private final Event<NotificationEvent> notificationEvent;

    private final SavePopUpPresenter savePopUpPresenter;

    // Sections
    private final DependenciesPresenter dependenciesSettingsSection;
    private final DeploymentsPresenter deploymentsSettingsSection;
    private final ExternalDataObjectsPresenter externalDataObjectsSettingsSection;
    private final GeneralSettingsPresenter generalSettingsSection;
    private final KnowledgeBasesPresenter knowledgeBasesSettingsSection;
    private final PersistencePresenter persistenceSettingsSection;
    private final ValidationPresenter validationSettingsSection;

    @Inject
    public SettingsPresenter(final View view,
                             final Event<NotificationEvent> notificationEvent,
                             final SavePopUpPresenter savePopUpPresenter,
                             final DependenciesPresenter dependenciesSettingsSection,
                             final DeploymentsPresenter deploymentsSettingsSection,
                             final ExternalDataObjectsPresenter externalDataObjectsSettingsSection,
                             final GeneralSettingsPresenter generalSettingsSection,
                             final KnowledgeBasesPresenter knowledgeBasesSettingsSection,
                             final PersistencePresenter persistenceSettingsSection,
                             final ValidationPresenter validationSettingsSection) {
        this.view = view;
        this.notificationEvent = notificationEvent;
        this.savePopUpPresenter = savePopUpPresenter;

        this.dependenciesSettingsSection = dependenciesSettingsSection;
        this.deploymentsSettingsSection = deploymentsSettingsSection;
        this.externalDataObjectsSettingsSection = externalDataObjectsSettingsSection;
        this.generalSettingsSection = generalSettingsSection;
        this.knowledgeBasesSettingsSection = knowledgeBasesSettingsSection;
        this.persistenceSettingsSection = persistenceSettingsSection;
        this.validationSettingsSection = validationSettingsSection;
    }

    //
    // Setup

    @PostConstruct
    public void setup() {
        view.init(this);
        view.showBusyIndicator();
        getSectionsInDisplayOrder().forEach(section -> section.setup(getView()));
        goTo(generalSettingsSection);
    }

    public void save() {
        Promises.reduceLazily(null,
                              getSectionsInDisplayOrder(),
                              Section::validate)
                .then(o -> {
                    savePopUpPresenter.show(comment -> executeSave(comment, DeploymentMode.VALIDATED));
                    return Promises.resolve();
                })
                .catch_(o -> {
                    view.hideBusyIndicator();
                    goTo((Section) o);
                    return Promises.resolve();
                });
    }

    private void executeSave(final String comment,
                             final DeploymentMode mode) {

        Promises.reduceLazilyChaining(null,
                                      getSectionsInDisplayOrder(),
                                      (chain, section) -> section.save(comment, mode, chain))
                .then(o -> {
                    view.hideBusyIndicator();
                    notificationEvent.fire(new NotificationEvent(view.getSaveSuccessMessage(), SUCCESS));
                    return Promises.resolve();
                })
                .catch_(o -> {
                    goTo(((SectionSaveError) o).section);
                    return Promises.resolve();
                });
    }

    public void reset() {
        setup();
    }

    public void goToGeneralSettingsSection() {
        goTo(generalSettingsSection);
    }

    public void goToDependenciesSection() {
        goTo(dependenciesSettingsSection);
    }

    public void goToKnowledgeBasesSection() {
        goTo(knowledgeBasesSettingsSection);
    }

    public void goToExternalDataObjectsSection() {
        goTo(externalDataObjectsSettingsSection);
    }

    public void goToValidationSection() {
        goTo(validationSettingsSection);
    }

    public void goToDeploymentsSection() {
        goTo(deploymentsSettingsSection);
    }

    public void goToPersistenceSection() {
        goTo(persistenceSettingsSection);
    }

    private void goTo(final Section section) {
        view.setContent(section.getView());
    }

    private List<Section> getSectionsInDisplayOrder() {
        return Arrays.asList(
                generalSettingsSection,
                dependenciesSettingsSection,
                knowledgeBasesSettingsSection,
                externalDataObjectsSettingsSection,
                validationSettingsSection,
                deploymentsSettingsSection,
                persistenceSettingsSection
        );
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "Settings Screen";
    }

    @WorkbenchPartView
    public View getView() {
        return view;
    }

    public interface Section {

        //FIXME: remove default
        default Promise<Object> save(final String comment,
                                     final DeploymentMode mode,
                                     final Supplier<Promise<Object>> saveChain) {

            DomGlobal.console.info("Saving " + getClass().getSimpleName());
            return Promises.resolve();
        }

        //FIXME: remove default
        default Promise<Object> validate() {
            DomGlobal.console.info("Validating " + getClass().getSimpleName());
            return Promise.resolve(true);
        }

        //FIXME: remove default
        default void setup(final HasBusyIndicator container) {
            DomGlobal.console.info("Setting up " + getClass().getSimpleName());
        }

        View.Section getView();
    }
}
