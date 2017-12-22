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

package org.kie.workbench.common.screens.library.client.settings.validation;

import java.util.List;

import javax.inject.Inject;

import elemental2.promise.Promise;
import org.guvnor.common.services.project.model.ProjectRepositories;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.screens.library.client.settings.Promises;
import org.kie.workbench.common.screens.library.client.settings.SettingsPresenter;
import org.kie.workbench.common.screens.projecteditor.model.ProjectScreenModel;

import static java.util.stream.Collectors.toList;

public class ValidationPresenter implements SettingsPresenter.Section {

    private final View view;
    private final ManagedInstance<ValidationItemPresenter> validationItemPresenters;

    private ProjectRepositories repositories;

    public interface View extends SettingsPresenter.View.Section<ValidationPresenter> {

        void setItems(final List<ValidationItemPresenter.View> view);
    }

    @Inject
    public ValidationPresenter(final ValidationPresenter.View view,
                               final ManagedInstance<ValidationItemPresenter> validationItemPresenters) {

        this.view = view;
        this.validationItemPresenters = validationItemPresenters;
    }

    @Override
    public Promise<Void> setup(final ProjectScreenModel model) {

        repositories = model.getRepositories();

        view.init(this);
        view.setItems(repositories.getRepositories()
                              .stream()
                              .map(repository -> validationItemPresenters.get().setup(repository).getView())
                              .collect(toList()));

        return Promises.resolve();
    }

    @Override
    public SettingsPresenter.View.Section getView() {
        return view;
    }
}
