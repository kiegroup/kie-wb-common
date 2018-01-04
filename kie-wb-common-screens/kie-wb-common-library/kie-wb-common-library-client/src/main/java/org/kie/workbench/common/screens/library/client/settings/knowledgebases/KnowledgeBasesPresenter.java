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

package org.kie.workbench.common.screens.library.client.settings.knowledgebases;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.kie.workbench.common.screens.library.client.settings.SettingsPresenter;
import org.kie.workbench.common.screens.library.client.settings.SettingsSectionChange;

public class KnowledgeBasesPresenter extends SettingsPresenter.Section {

    private final View view;

    public interface View extends SettingsPresenter.View.Section<KnowledgeBasesPresenter> {

    }

    @Inject
    public KnowledgeBasesPresenter(final Event<SettingsSectionChange> settingsSectionChangeEvent,
                                   final KnowledgeBasesPresenter.View view) {

        super(settingsSectionChangeEvent);
        this.view = view;
    }

    @Override
    public SettingsPresenter.View.Section getView() {
        return view;
    }

    @Override
    public int currentHashCode() {
        return 0;
    }
}
