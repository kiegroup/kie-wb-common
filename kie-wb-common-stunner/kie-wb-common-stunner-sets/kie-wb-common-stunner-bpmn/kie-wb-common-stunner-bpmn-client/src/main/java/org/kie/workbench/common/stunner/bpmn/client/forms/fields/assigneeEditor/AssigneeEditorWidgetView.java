/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.client.forms.fields.assigneeEditor;

import java.util.List;

import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.AssigneeRow;
import org.kie.workbench.common.stunner.bpmn.client.forms.util.ListBoxValues;
import org.kie.workbench.common.stunner.bpmn.forms.model.AssigneeType;

public interface AssigneeEditorWidgetView {

    interface Presenter {

        AssigneeType getType();

        Integer getMax();

        void doSave();

        void notifyModelChanged();

        List<AssigneeRow> deserializeAssignees(final String s);

        String serializeAssignees(final List<AssigneeRow> assigneeRows);

        void setNames(final List<String> names);

        void addAssignee();

        boolean isDuplicateName(final String name);

        void removeAssignee(final AssigneeRow assigneeRow);

        ListBoxValues.ValueTester namesTester();
    }

    void init(final Presenter presenter);

    void doSave();

    int getAssigneeRowsCount();

    void setTableDisplayStyle();

    void setNoneDisplayStyle();

    void setAssigneesNames(final ListBoxValues nameListBoxValues);

    void setAssigneeRows(final List<AssigneeRow> rows);

    List<AssigneeRow> getAssigneeRows();

    AssigneeListItemWidgetView getAssigneeWidget(final int index);

    boolean isDuplicateName(final String name);

    void showMaxAssigneesAdded();
}
