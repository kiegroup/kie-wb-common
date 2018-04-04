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

package org.kie.workbench.common.stunner.bpmn.definition;

import javax.validation.Valid;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.NonPortable;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.forms.adf.definitions.annotations.FieldParam;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormDefinition;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormField;
import org.kie.workbench.common.forms.adf.definitions.settings.FieldPolicy;
import org.kie.workbench.common.stunner.bpmn.definition.property.background.BackgroundSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.DataIOSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.dimensions.CircleDimensionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.dimensions.Radius;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.error.ErrorEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.font.FontSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.BPMNGeneralSet;
import org.kie.workbench.common.stunner.core.definition.annotation.Definition;
import org.kie.workbench.common.stunner.core.definition.annotation.PropertySet;
import org.kie.workbench.common.stunner.core.definition.annotation.morph.Morph;
import org.kie.workbench.common.stunner.core.definition.builder.Builder;
import org.kie.workbench.common.stunner.core.factory.graph.NodeFactory;
import org.kie.workbench.common.stunner.core.util.HashUtil;

@Portable
@Bindable
@Definition(graphFactory = NodeFactory.class, builder = EndErrorEvent.EndNoneEventBuilder.class)
@Morph(base = BaseEndEvent.class)
@FormDefinition(
        startElement = "general",
        policy = FieldPolicy.ONLY_MARKED,
        defaultFieldSettings = {@FieldParam(name = "nestedFormContainer", value = "COLLAPSIBLE")}
)
public class EndErrorEvent extends BaseEndEvent {

    @PropertySet
    @FormField(afterElement = "general")
    @Valid
    protected ErrorEventExecutionSet executionSet;

    @PropertySet
    @FormField(afterElement = "executionSet")
    protected DataIOSet dataIOSet;

    @NonPortable
    public static class EndNoneEventBuilder implements Builder<EndErrorEvent> {

        @Override
        public EndErrorEvent build() {
            return new EndErrorEvent(new BPMNGeneralSet(""),
                                     new BackgroundSet(),
                                     new FontSet(),
                                     new CircleDimensionSet(new Radius()),
                                     new ErrorEventExecutionSet(),
                                     new DataIOSet());
        }
    }

    public EndErrorEvent() {
    }

    public EndErrorEvent(final @MapsTo("general") BPMNGeneralSet general,
                         final @MapsTo("backgroundSet") BackgroundSet backgroundSet,
                         final @MapsTo("fontSet") FontSet fontSet,
                         final @MapsTo("dimensionsSet") CircleDimensionSet dimensionsSet,
                         final @MapsTo("executionSet") ErrorEventExecutionSet executionSet,
                         final @MapsTo("dataIOSet") DataIOSet dataIOSet) {
        super(general,
              backgroundSet,
              fontSet,
              dimensionsSet);
        this.executionSet = executionSet;
        this.dataIOSet = dataIOSet;
    }

    @Override
    public boolean hasInputVars() {
        return true;
    }

    @Override
    public boolean isSingleInputVar() {
        return true;
    }

    public ErrorEventExecutionSet getExecutionSet() {
        return executionSet;
    }

    public void setExecutionSet(ErrorEventExecutionSet executionSet) {
        this.executionSet = executionSet;
    }

    public DataIOSet getDataIOSet() {
        return dataIOSet;
    }

    public void setDataIOSet(DataIOSet dataIOSet) {
        this.dataIOSet = dataIOSet;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EndErrorEvent)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        EndErrorEvent that = (EndErrorEvent) o;

        if (executionSet != null ? !executionSet.equals(that.executionSet) : that.executionSet != null) {
            return false;
        }
        return dataIOSet != null ? dataIOSet.equals(that.dataIOSet) : that.dataIOSet == null;
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(super.hashCode(),
                                         executionSet.hashCode(),
                                         dataIOSet.hashCode());
    }
}
