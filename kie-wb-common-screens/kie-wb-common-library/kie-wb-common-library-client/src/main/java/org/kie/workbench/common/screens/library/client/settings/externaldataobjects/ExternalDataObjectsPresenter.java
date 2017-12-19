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

import javax.inject.Inject;

import org.kie.workbench.common.screens.library.client.settings.SettingsPresenter;

public class ExternalDataObjectsPresenter implements SettingsPresenter.Section {

    private final View view;

    public interface View extends SettingsPresenter.View.Section<ExternalDataObjectsPresenter> {

    }

    @Inject
    public ExternalDataObjectsPresenter(final ExternalDataObjectsPresenter.View view) {
        this.view = view;
    }

    @Override
    public void beforeSave() {

    }

    @Override
    public SettingsPresenter.View.Section getView() {
        return view;
    }
}
