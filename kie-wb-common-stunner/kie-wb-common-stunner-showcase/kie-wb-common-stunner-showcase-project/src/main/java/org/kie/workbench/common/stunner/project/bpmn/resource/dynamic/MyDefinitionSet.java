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

import javax.enterprise.context.ApplicationScoped;

import org.jboss.errai.common.client.api.annotations.NonPortable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.stunner.bpmn.factory.BPMNGraphFactory;
import org.kie.workbench.common.stunner.bpmn.qualifiers.BPMN;
import org.kie.workbench.common.stunner.core.definition.annotation.DefinitionSet;
import org.kie.workbench.common.stunner.core.definition.annotation.Description;
import org.kie.workbench.common.stunner.core.definition.builder.Builder;
import org.kie.workbench.common.stunner.core.rule.annotation.CanContain;
import org.kie.workbench.common.stunner.core.rule.annotation.Occurrences;

@ApplicationScoped
@Bindable
@DefinitionSet(
        graphFactory = BPMNGraphFactory.class,
        qualifier = BPMN.class,
        definitions = {
                MyBusinessRuleTask.class
        },
        builder = MyDefinitionSet.BPMNDefinitionSetBuilder.class,
        addonGroups = {BPMN.class}
)
@CanContain(roles = {"diagram"})
@Occurrences(role = "diagram", max = 1)
@Occurrences(role = "Startevents_all", min = 1)
@Occurrences(role = "Endevents_all", min = 1)
public class MyDefinitionSet {

    @Description
    public static final transient String description = "BPMN2";

    @NonPortable
    public static class BPMNDefinitionSetBuilder implements Builder<MyDefinitionSet> {

        @Override
        public MyDefinitionSet build() {
            return new MyDefinitionSet();
        }
    }

    public MyDefinitionSet() {
    }

    public String getDescription() {
        return description;
    }
}
