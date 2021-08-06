/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.validation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

import org.kie.workbench.common.stunner.bpmn.BPMNDefinitionSet;
import org.kie.workbench.common.stunner.bpmn.definition.ReusableSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.AssignmentsInfo;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.validation.DomainValidator;
import org.kie.workbench.common.stunner.core.validation.DomainViolation;
import org.kie.workbench.common.stunner.core.validation.Violation;

public abstract class BPMNReusableSubProcessValidator implements DomainValidator {

    @Override
    public String getDefinitionSetId() {
        return BindableAdapterUtils.getDefinitionSetId(BPMNDefinitionSet.class);
    }

    @Override
    public void validate(Diagram diagram, Consumer<Collection<DomainViolation>> resultConsumer) {
        Iterator<Element> it = diagram.getGraph().nodes().iterator();
        List<DomainViolation> violations = new ArrayList<>();
        while (it.hasNext()) {
            Element element = it.next();
            if (element.getContent() instanceof View && ((View) element.getContent()).getDefinition() instanceof ReusableSubprocess) {
                ReusableSubprocess subprocess = (ReusableSubprocess) ((View) element.getContent()).getDefinition();
                final AssignmentsInfo assignmentsInfo = subprocess.getDataIOSet().getAssignmentsinfo();
                if (hasNoAssignmentsDataInput(assignmentsInfo) || hasNoAssignmentsDataOutput(assignmentsInfo)) {
                    BPMNViolation bpmnViolation = new BPMNViolation(getMessageSubprocessWithoutDataIOAssignments() + " : " + subprocess.getGeneral().getName().getValue(), Violation.Type.WARNING, element.getUUID());
                    violations.add(bpmnViolation);
                }
            }
        }
        resultConsumer.accept(violations);
    }

    public abstract String getMessageSubprocessWithoutDataIOAssignments();

    public abstract boolean hasNoAssignmentsDataInput(AssignmentsInfo assignmentsInfo);

    public abstract boolean hasNoAssignmentsDataOutput(AssignmentsInfo assignmentsInfo);
}