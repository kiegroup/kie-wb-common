/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.client.forms.fields.assigneeEditor.widget;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Logger;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.bpmn.forms.model.AssigneeType;
import org.uberfire.ext.widgets.common.client.dropdown.EntryCreationLiveSearchService;
import org.uberfire.ext.widgets.common.client.dropdown.LiveSearchCallback;

import static org.kie.workbench.common.stunner.core.util.StringUtils.isEmpty;

// TODO: (Submarine) Re-implement by using ClientUserSystemManager

@Dependent
public class AssigneeLiveSearchService implements EntryCreationLiveSearchService<String, AssigneeLiveSearchEntryCreationEditor> {

    private static final Logger LOGGER = Logger.getLogger(AssigneeLiveSearchService.class.getName());

    private AssigneeType type = AssigneeType.USER;

    private AssigneeLiveSearchEntryCreationEditor editor;

    private List<String> customEntries = new ArrayList<>();

    private Consumer<Throwable> searchErrorHandler;

    public AssigneeLiveSearchService() {
    /* due to spotbugs error commented out
       this(null, null); */
    }

    @Inject
    public AssigneeLiveSearchService(final AssigneeLiveSearchEntryCreationEditor editor) {
        this.editor = editor;

        editor.setCustomEntryCommand(this::addCustomEntry);
    }

    public void init(AssigneeType type) {
        this.type = type;
    }

    public void addCustomEntry(String customEntry) {
        if (!isEmpty(customEntry)) {
            customEntries.add(customEntry);
        }
    }

    public void setSearchErrorHandler(Consumer<Throwable> searchErrorHandler) {
        this.searchErrorHandler = searchErrorHandler;
    }

    @Override
    public void search(final String pattern, final int maxResults, final LiveSearchCallback<String> callback) {

        // TODO

    }

    @Override
    public void searchEntry(String key, LiveSearchCallback<String> callback) {

        // TOD

    }

    @Override
    public AssigneeLiveSearchEntryCreationEditor getEditor() {
        return editor;
    }
}
