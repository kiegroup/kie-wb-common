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

package org.kie.workbench.common.stunner.bpmn.definition.property.assignee;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.forms.adf.definitions.annotations.metaModel.FieldDefinition;
import org.kie.workbench.common.forms.adf.definitions.annotations.metaModel.FieldValue;
import org.kie.workbench.common.forms.adf.definitions.annotations.metaModel.I18nMode;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNProperty;
import org.kie.workbench.common.stunner.core.definition.annotation.Property;
import org.kie.workbench.common.stunner.core.definition.annotation.property.Value;

@Portable
@Bindable
@Property
@FieldDefinition(i18nMode = I18nMode.OVERRIDE_I18N_KEY)
public class Actors implements BPMNProperty {

    @Value
    @FieldValue
    private String value;
    private Collection<String> actors;

    public Actors() {
        this("");
    }

    public Actors(final String value) {
        this.value = value;
        this.actors = parse(value);
    }

    public Actors(final Collection<String> actors) {
        this.value = render(actors);
        this.actors = actors;
    }

    private List<String> parse(String value) {
        return Arrays.asList(value.split(","));
    }

    private String render(final Collection<String> actors) {
        return actors.stream().collect(Collectors.joining(","));
    }

    public Collection<String> getActors() {
        return actors;
    }

    public void setActors(Collection<String> actors) {
        this.actors = actors;
        this.value = render(actors);
    }

    public String getValue() {
        return value;
    }

    public void setValue(final String value) {
        this.value = value;
        this.actors = parse(value);
    }

    @Override
    public int hashCode() {
        return (null != value) ? value.hashCode() : 0;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Actors) {
            Actors other = (Actors) o;
            return (null != value) ? value.equals(other.value) : null == other.value;
        }
        return false;
    }
}
