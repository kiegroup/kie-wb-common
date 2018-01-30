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

package org.kie.workbench.common.stunner.bpmn.backend.converters.properties;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.bpmn2.Assignment;
import org.eclipse.bpmn2.DataInput;
import org.eclipse.bpmn2.DataInputAssociation;
import org.eclipse.bpmn2.FormalExpression;
import org.eclipse.bpmn2.PotentialOwner;
import org.eclipse.bpmn2.ResourceRole;
import org.eclipse.bpmn2.UserTask;

public class UserTaskPropertyReader extends AbstractPropertyReader {

    private final UserTask task;

    public UserTaskPropertyReader(UserTask element) {
        super(element);
        this.task = element;
    }

    public String getTaskName() {
        return input("TaskName");
    }

    public String getActors() {
        // get the user task actors
        List<ResourceRole> roles = task.getResources();
        List<String> users = new ArrayList<>();
        for (ResourceRole role : roles) {
            if (role instanceof PotentialOwner) {
                FormalExpression fe = (FormalExpression) role.getResourceAssignmentExpression().getExpression();
                users.add(fe.getBody());
            }
        }
        return users.stream().collect(Collectors.joining(","));
    }

    public String getGroupid() {
        return input("GroupId");
    }

    public String getAssignmentsInfo() {
        return Properties.getAssignmentsInfo(task);
    }

    public boolean isAsync() {
        return Boolean.parseBoolean(metaData("customAsync"));
    }

    public boolean isSkippable() {
        return Boolean.parseBoolean(input("Skippable"));
    }

    public String getPriority() {
        return input("Priority");
    }

    public String getSubject() {
        return input("Comment");
    }

    public String getDescription() {
        return input("Description");
    }

    public String getCreatedBy() {
        return input("CreatedBy");
    }

    public boolean isAdHocAutostart() {
        return Boolean.parseBoolean(metaData("customAutoStart"));
    }

    public String getOnEntryAction() {
        return Scripts.onEntry(element.getExtensionValues());
    }

    public String getOnExitAction() {
        return Scripts.onExit(element.getExtensionValues());
    }

    public String getScriptLanguage() {
        return Scripts.scriptLanguage(element.getExtensionValues());
    }

    public String input(String name) {
        for (DataInputAssociation din : task.getDataInputAssociations()) {
            DataInput targetRef = (DataInput) (din.getTargetRef());
            if (targetRef.getName().equalsIgnoreCase(name)) {
                Assignment assignment = din.getAssignment().get(0);
                return evaluate(assignment).toString();
            }
        }
        return "";
    }

    private static Object evaluate(Assignment assignment) {
        return ((FormalExpression) assignment.getFrom()).getMixed().getValue(0);
    }
}
