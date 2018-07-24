/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.forms.cms.components.client.ui.crud;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.Dependent;

import org.kie.workbench.common.forms.cms.components.client.ui.SettingsReader;
import org.kie.workbench.common.forms.cms.components.shared.model.crud.CRUDSettings;

@Dependent
public class CRUDSettingsReader extends SettingsReader<CRUDSettings> {

    public static final String CREATION_FORM = "creationForm";
    public static final String EDITION_FORM = "editionForm";
    public static final String PREVIEW_FORM = "previewForm";
    public static final String TABLE_FORM = "tableForm";

    @Override
    protected Map<String, String> writeToMap(CRUDSettings settings) {
        Map<String, String> settingsMap = new HashMap<>();

        settingsMap.put(CREATION_FORM, settings.getCreationForm());
        settingsMap.put(EDITION_FORM, settings.getEditionForm());
        settingsMap.put(PREVIEW_FORM, settings.getPreviewForm());
        settingsMap.put(TABLE_FORM, settings.getTableForm());

        return settingsMap;
    }

    @Override
    protected CRUDSettings readFromMap(Map<String, String> settingsMap) {
        CRUDSettings settings = new CRUDSettings();

        settings.setCreationForm(settingsMap.get(CREATION_FORM));
        settings.setEditionForm(settingsMap.get(EDITION_FORM));
        settings.setPreviewForm(settingsMap.get(PREVIEW_FORM));
        settings.setTableForm(settingsMap.get(TABLE_FORM));

        return settings;
    }
}
