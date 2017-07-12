/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.project.bpmn.resource.dynamic;

import java.util.HashSet;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;

import org.kie.workbench.common.stunner.bpmn.definition.BaseTask;
import org.kie.workbench.common.stunner.bpmn.definition.BusinessRuleTask;
import org.kie.workbench.common.stunner.bpmn.qualifiers.BPMN;
import org.kie.workbench.common.stunner.core.definition.DynamicDefinition;
import org.kie.workbench.common.stunner.core.definition.builder.Builder;
import org.kie.workbench.common.stunner.core.factory.graph.NodeFactory;

@ApplicationScoped
public class MyBusinessRuleTaskDef implements DynamicDefinition {

    @Override
    public Class getType() {
        return MyBusinessRuleTask.class;
    }

    @Override
    public Class getBaseType() {
        return BaseTask.class;
    }

    @Override
    public Class getFactory() {
        return NodeFactory.class;
    }

    @Override
    public Builder<?> getBuilder() {
        return new MyBusinessRuleTask.MyBusinessRuleTaskBuilder();
    }

    @Override
    public Set<String> getSetProperties() {
        return new HashSet<String>() {
            {
                add("myProperty");
                add("executionSet");
                add("dataIOSet");
                add("general");
                add("backgroundSet");
                add("fontSet");
                add("simulationSet");
                add("dimensionsSet");
            }
        };
    }

    @Override
    public Set<String> getProperties() {
        return new HashSet<String>() {
            {
                add("taskType");
            }
        };
    }

    @Override
    public Set<Class<?>> getAddonGroups() {
        return new HashSet<Class<?>>() {
            {
                add(BPMN.class);
            }
        };
    }
}
