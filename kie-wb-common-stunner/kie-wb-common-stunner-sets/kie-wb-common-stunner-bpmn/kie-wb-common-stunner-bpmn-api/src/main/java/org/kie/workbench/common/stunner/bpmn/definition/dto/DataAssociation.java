/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.definition.dto;

import org.kie.workbench.common.stunner.bpmn.definition.BPMNProperty;
import org.kie.workbench.common.stunner.bpmn.definition.UserTask;

public class DataAssociation<T extends DataAssociation> implements BPMNProperty {

    private SourceRef sourceRef;

    private TargetRef targetRef;

    private Assignment assignment;

    public DataAssociation() {

    }

    public DataAssociation(UserTask userTask, String value, String postfix) {
        targetRef = new TargetRef(userTask.getId() + "_" + postfix);
        assignment = new Assignment(value, userTask.getId(), postfix);
    }

    public SourceRef getSourceRef() {
        return sourceRef;
    }

    public T setSourceRef(SourceRef sourceRef) {
        this.sourceRef = sourceRef;
        return (T) this;
    }

    public TargetRef getTargetRef() {
        return targetRef;
    }

    public T setTargetRef(TargetRef targetRef) {
        this.targetRef = targetRef;
        return (T) this;
    }

    public Assignment getAssignment() {
        return assignment;
    }

    public T setAssignment(Assignment assignment) {
        this.assignment = assignment;
        return (T) this;
    }

}
