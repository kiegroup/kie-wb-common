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

package org.kie.workbench.common.stunner.bpmn.project.backend.service;

import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.bpmn.definition.property.task.RuleFlowGroup;
import org.kie.workbench.common.stunner.bpmn.forms.dataproviders.RequestRuleFlowGroupDataEvent;
import org.kie.workbench.common.stunner.bpmn.forms.dataproviders.RuleFlowGroupDataEvent;

@ApplicationScoped
public class RuleFlowGroupDataService {

    private final RuleFlowGroupQueryService queryService;
    private final Event<RuleFlowGroupDataEvent> dataChangedEvent;

    @Inject
    public RuleFlowGroupDataService(final RuleFlowGroupQueryService queryService,
                                    final Event<RuleFlowGroupDataEvent> dataChangedEvent) {
        this.queryService = queryService;
        this.dataChangedEvent = dataChangedEvent;
    }

    public List<RuleFlowGroup> getRuleFlowGroupNames() {
        final List<String> groupNames = queryService.getRuleFlowGroupNames();
        return groupNames.stream().map(RuleFlowGroup::new).collect(Collectors.toList());
    }

    void onRequestRuleFlowGroupDataEvent(@Observes final RequestRuleFlowGroupDataEvent event) {
        fireData();
    }

    void fireData() {
        // TODO Can we send List directly?
        final RuleFlowGroup[] groupNames = getRuleFlowGroupNames().toArray(new RuleFlowGroup[0]);
        dataChangedEvent.fire(new RuleFlowGroupDataEvent(groupNames));
    }
}
