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

package org.kie.workbench.common.stunner.bpmn.client.dataproviders;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.kie.workbench.common.forms.dynamic.model.config.SelectorData;
import org.kie.workbench.common.forms.dynamic.model.config.SelectorDataProvider;
import org.kie.workbench.common.forms.dynamic.service.shared.FormRenderingContext;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.RuleFlowGroup;
import org.kie.workbench.common.stunner.bpmn.forms.dataproviders.RequestRuleFlowGroupDataEvent;

@Dependent
public class RuleFlowGroupFormProvider implements SelectorDataProvider {

    @Inject
    Event<RequestRuleFlowGroupDataEvent> requestRuleFlowGroupDataEvent;

    @Inject
    RuleFlowGroupDataProvider dataProvider;

    @Override
    public String getProviderName() {
        return getClass().getSimpleName();
    }

    @Override
    @SuppressWarnings("unchecked")
    public SelectorData getSelectorData(final FormRenderingContext context) {
        requestRuleFlowGroupDataEvent.fire(new RequestRuleFlowGroupDataEvent());
        return new SelectorData(toMap(dataProvider.getRuleFlowGroupNames()), null);
    }

    // Map<T, String> is not supported by ListBoxValue which is used for ComboBox widget
    private static Map<String, String> toMap(final Iterable<RuleFlowGroup> groups) {
        Map<String, String> result = new HashMap<>();
        for (RuleFlowGroup group : groups) {
            if (result.containsKey(group.getName())) {
                String project = getProjectFromPath(group.getPathUri());
                if (!result.get(group.getName()).contains(project)) {
                    result.put(group.getName(), addProjectToDescription(result.get(group.getName()), project));
                }
            } else {
                result.put(group.getName(), getGroupDescription(group));
            }
        }
        return result;
    }

    private static String addProjectToDescription(String description, String project) {
        return description.replace("]", "," + project + "]");
    }

    private static String getGroupDescription(RuleFlowGroup rfg) {
        return rfg.getName() + " [" + getSpaceAndProjectFromPath(rfg.getPathUri()) + "]";
    }

    private static String getSpaceAndProjectFromPath(String path) {
        return getSpaceFromPath(path) + " " + getProjectFromPath(path);
    }

    private static String getSpaceFromPath(String path) {
        String clearedPath = dropFileSystemAndGitBranchFromPath(path);
        return clearedPath.substring(0, getIndexOfFileSeparator(path) - 1);
    }

    private static String dropFileSystemAndGitBranchFromPath(String path) {
        return path.substring(path.indexOf('@') + 1);
    }

    private static String getProjectFromPath(String path) {
        String clearedPath = dropFileSystemAndGitBranchFromPath(path);
        //Drop space
        String pathAfterSpace = clearedPath.substring(getIndexOfFileSeparator(clearedPath) + 1);
        return pathAfterSpace.substring(0, getIndexOfFileSeparator(pathAfterSpace));
    }

    // GWT compatible way to get fileseparation for Windows/Unix
    private static int getIndexOfFileSeparator(String string) {
        int index = string.indexOf('/');
        if (index == -1) {
            return string.indexOf('\\');
        }
        return index;
    }
}
