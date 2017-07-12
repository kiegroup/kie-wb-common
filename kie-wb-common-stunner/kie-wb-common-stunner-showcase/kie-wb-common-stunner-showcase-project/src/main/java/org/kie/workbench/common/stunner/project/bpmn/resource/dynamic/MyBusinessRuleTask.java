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

import javax.enterprise.context.Dependent;
import javax.validation.Valid;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.NonPortable;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormDefinition;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormField;
import org.kie.workbench.common.forms.adf.definitions.settings.FieldPolicy;
import org.kie.workbench.common.stunner.bpmn.definition.BaseTask;
import org.kie.workbench.common.stunner.bpmn.definition.property.background.BackgroundSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.DataIOSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.dimensions.RectangleDimensionsSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.font.FontSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Documentation;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Name;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.TaskGeneralSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.simulation.SimulationSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.BusinessRuleTaskExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.TaskType;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.TaskTypes;
import org.kie.workbench.common.stunner.bpmn.qualifiers.BPMN;
import org.kie.workbench.common.stunner.core.definition.annotation.Definition;
import org.kie.workbench.common.stunner.core.definition.annotation.PropertySet;
import org.kie.workbench.common.stunner.core.definition.annotation.definition.Title;
import org.kie.workbench.common.stunner.core.definition.annotation.morph.Morph;
import org.kie.workbench.common.stunner.core.factory.graph.NodeFactory;
import org.kie.workbench.common.stunner.core.rule.annotation.CanDock;
import org.kie.workbench.common.stunner.core.util.HashUtil;

@Portable
@Bindable
@Definition(
        graphFactory = NodeFactory.class,
        builder = MyBusinessRuleTask.MyBusinessRuleTaskBuilder.class,
        addonGroups = {BPMN.class}
)
@CanDock(roles = {"IntermediateEventOnActivityBoundary"})
@Morph(base = BaseTask.class)
@FormDefinition(
        startElement = "general",
        policy = FieldPolicy.ONLY_MARKED
)
@Dependent
public class MyBusinessRuleTask extends BaseTask {

    @Title
    public static final transient String title = "My Business Rule Task";

    @PropertySet
    @FormField(
            afterElement = "general"
    )
    @Valid
    protected BusinessRuleTaskExecutionSet executionSet;

    @PropertySet
    @FormField(
            afterElement = "executionSet"
    )
    @Valid
    protected DataIOSet dataIOSet;

    public BusinessRuleTaskExecutionSet getExecutionSet() {
        return executionSet;
    }

    public void setExecutionSet(BusinessRuleTaskExecutionSet executionSet) {
        this.executionSet = executionSet;
    }

    public DataIOSet getDataIOSet() {
        return dataIOSet;
    }

    public void setDataIOSet(DataIOSet dataIOSet) {
        this.dataIOSet = dataIOSet;
    }

    public DataIOSet getMyProperty() {
        return myProperty;
    }

    public void setMyProperty(DataIOSet myProperty) {
        this.myProperty = myProperty;
    }

    @PropertySet
    @FormField(
            afterElement = "dataIOSet"
    )
    @Valid
    protected DataIOSet myProperty;

    @Override
    public String getTitle() {
        return title;
    }


    @NonPortable
    public static class MyBusinessRuleTaskBuilder extends BaseTaskBuilder<MyBusinessRuleTask> {

        @Override
        public MyBusinessRuleTask build() {
            return new MyBusinessRuleTask(new TaskGeneralSet(new Name("Task"),
                                                             new Documentation("")),
                                          new BusinessRuleTaskExecutionSet(),
                                          new DataIOSet(),
                                          new DataIOSet(),
                                          new BackgroundSet(COLOR,
                                                            BORDER_COLOR,
                                                            BORDER_SIZE),
                                          new FontSet(),
                                          new RectangleDimensionsSet(WIDTH,
                                                                     HEIGHT),
                                          new SimulationSet(),
                                          new TaskType(TaskTypes.NONE)
            );
        }
    }

    public MyBusinessRuleTask() {
        super(TaskTypes.NONE);
    }

    public MyBusinessRuleTask(final @MapsTo("general") TaskGeneralSet general,
                              final @MapsTo("executionSet") BusinessRuleTaskExecutionSet executionSet,
                              final @MapsTo("dataIOSet") DataIOSet dataIOSet,
                              final @MapsTo("myProperty") DataIOSet myProperty,
                              final @MapsTo("backgroundSet") BackgroundSet backgroundSet,
                              final @MapsTo("fontSet") FontSet fontSet,
                              final @MapsTo("dimensionsSet") RectangleDimensionsSet dimensionsSet,
                              final @MapsTo("simulationSet") SimulationSet simulationSet,
                              final @MapsTo("taskType") TaskType taskType) {

        super(general,
              backgroundSet,
              fontSet,
              dimensionsSet,
              simulationSet,
              taskType);

        this.executionSet = executionSet;
        this.dataIOSet = dataIOSet;
        this.myProperty = myProperty;
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(super.hashCode(),
                                         executionSet.hashCode(),
                                         dataIOSet.hashCode(),
                                         myProperty.hashCode());
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof MyBusinessRuleTask) {
            MyBusinessRuleTask other = (MyBusinessRuleTask) o;
            return super.equals(other) &&
                    myProperty.equals(other.myProperty) &&
                    dataIOSet.equals(other.dataIOSet) &&
                    executionSet.equals(other.executionSet);
        }
        return false;
    }
}
