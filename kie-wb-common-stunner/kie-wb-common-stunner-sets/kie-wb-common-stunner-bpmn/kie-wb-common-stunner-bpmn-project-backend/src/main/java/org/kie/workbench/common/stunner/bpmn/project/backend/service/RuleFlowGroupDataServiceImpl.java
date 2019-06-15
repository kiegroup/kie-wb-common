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

import java.util.LinkedList;
import java.util.List;

import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.errai.bus.server.annotations.Service;
import org.kie.workbench.common.stunner.bpmn.forms.dataproviders.ruleflow.RuleFlowGroupDataChangedEvent;
import org.kie.workbench.common.stunner.bpmn.project.service.RuleFlowGroupDataService;

@ApplicationScoped
@Service
public class RuleFlowGroupDataServiceImpl implements RuleFlowGroupDataService {

    private final RuleFlowGroupQueryService queryService;
    private final Event<RuleFlowGroupDataChangedEvent> groupNamesChangedEvent;
    final List<String> groupNames;

    @Inject
    public RuleFlowGroupDataServiceImpl(final RuleFlowGroupQueryService queryService,
                                        final Event<RuleFlowGroupDataChangedEvent> groupNamesChangedEvent) {
        this.queryService = queryService;
        this.groupNamesChangedEvent = groupNamesChangedEvent;
        this.groupNames = new LinkedList<>();
    }

    @Override
    public void checkForUpdates() {
        final List<String> latestValues = getRuleFlowGroupNames();
        synchronized (groupNames) {
            if (!groupNames.equals(latestValues)) {
                groupNames.clear();
                groupNames.addAll(latestValues);
                groupNamesChangedEvent.fire(new RuleFlowGroupDataChangedEvent(groupNames.toArray(new String[groupNames.size()])));
            }
        }
    }

    @Override
    public List<String> getRuleFlowGroupNames() {
        return queryService.getRuleFlowGroupNames();
    }

    @PreDestroy
    public void destroy() {
        groupNames.clear();
    }
}
