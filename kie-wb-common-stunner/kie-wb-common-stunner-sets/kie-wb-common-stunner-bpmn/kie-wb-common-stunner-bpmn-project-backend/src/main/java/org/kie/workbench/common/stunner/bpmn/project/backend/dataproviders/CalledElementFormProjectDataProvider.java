/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.project.backend.dataproviders;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Specializes;
import javax.inject.Inject;

import org.kie.soup.commons.util.Sets;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueResourceIndexTerm;
import org.kie.workbench.common.services.refactoring.model.query.RefactoringPageRow;
import org.kie.workbench.common.services.refactoring.service.RefactoringQueryService;
import org.kie.workbench.common.services.refactoring.service.ResourceType;
import org.kie.workbench.common.stunner.bpmn.backend.dataproviders.CalledElementFormDataProvider;
import org.kie.workbench.common.stunner.bpmn.project.backend.query.FindBpmnProcessIdsQuery;
import org.uberfire.backend.vfs.Path;

@Dependent
@Specializes
public class CalledElementFormProjectDataProvider extends CalledElementFormDataProvider {

    @Inject
    private RefactoringQueryService queryService;

    public void setQueryService(final RefactoringQueryService queryService) {
        this.queryService = queryService;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<Object, String> getBusinessProcessIDs() {
        final Set<ValueIndexTerm> queryTerms = new Sets.Builder<ValueIndexTerm>()
                .add(new ValueResourceIndexTerm("*",
                                                getProcessIdResourceType(),
                                                ValueIndexTerm.TermSearchType.WILDCARD))
                .build();

        List<RefactoringPageRow> results = queryService.query(
                getQueryName(),
                queryTerms);

        Map<Object, String> businessProcessIDs = new TreeMap<>();

        for (RefactoringPageRow row : results) {
            Map<String, Path> mapRow = (Map<String, Path>) row.getValue();
            for (String rKey : mapRow.keySet()) {
                businessProcessIDs.put(rKey,
                                       rKey);
            }
        }

        return businessProcessIDs;
    }

    public ResourceType getProcessIdResourceType() {
        return ResourceType.BPMN2;
    }

    public String getQueryName() {
        return FindBpmnProcessIdsQuery.NAME;
    }
}
