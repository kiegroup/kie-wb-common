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

package org.kie.workbench.common.stunner.bpmn.client.validation;

import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.AssignmentParser;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.AssignmentsInfo;
import org.kie.workbench.common.stunner.bpmn.validation.BPMNReusableSubProcessValidator;
import org.kie.workbench.common.stunner.client.widgets.resources.i18n.StunnerWidgetsConstants;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;

@ApplicationScoped
public class BPMNReusableSubProcessClientValidator extends BPMNReusableSubProcessValidator {

    private final ClientTranslationService translationService;

    @Inject
    public BPMNReusableSubProcessClientValidator(final ClientTranslationService translationService) {
        this.translationService = translationService;
    }

    @Override
    public String getMessageSubprocessWithoutDataIOAssignments() {
        return translationService.getValue(StunnerWidgetsConstants.MarshallingResponsePopup_ReusableSubprocessWithoutDataIO);
    }

    @Override
    public boolean hasNoAssignmentsDataInput(AssignmentsInfo assignmentsInfo) {
        return getAssignmentsDataInput(assignmentsInfo) == null;
    }

    @Override
    public boolean hasNoAssignmentsDataOutput(AssignmentsInfo assignmentsInfo) {
        return getAssignmentsDataOutput(assignmentsInfo) == null;
    }

    private Map<String, String> getAssignmentsMap(AssignmentsInfo assignmentsInfo) {
        return AssignmentParser.parseAssignmentsInfo(assignmentsInfo.getValue());
    }

    private String getAssignmentsDataInput(AssignmentsInfo assignmentsInfo) {
        return getAssignmentsMap(assignmentsInfo).get(AssignmentParser.DATAINPUTSET);
    }

    private String getAssignmentsDataOutput(AssignmentsInfo assignmentsInfo) {
        return getAssignmentsMap(assignmentsInfo).get(AssignmentParser.DATAOUTPUTSET);
    }
}