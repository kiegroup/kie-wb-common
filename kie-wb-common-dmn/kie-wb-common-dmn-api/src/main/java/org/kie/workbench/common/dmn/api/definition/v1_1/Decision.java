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
package org.kie.workbench.common.dmn.api.definition.v1_1;

import java.util.HashSet;
import java.util.Set;

import javax.validation.Valid;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.dmn.api.definition.DMNViewDefinition;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasVariable;
import org.kie.workbench.common.dmn.api.property.background.BackgroundSet;
import org.kie.workbench.common.dmn.api.property.dimensions.RectangleDimensionsSet;
import org.kie.workbench.common.dmn.api.property.dmn.AllowedAnswers;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.dmn.Question;
import org.kie.workbench.common.dmn.api.property.font.FontSet;
import org.kie.workbench.common.forms.adf.definitions.annotations.FieldParam;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormDefinition;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormField;
import org.kie.workbench.common.forms.adf.definitions.settings.FieldPolicy;
import org.kie.workbench.common.stunner.core.definition.annotation.Definition;
import org.kie.workbench.common.stunner.core.definition.annotation.Property;
import org.kie.workbench.common.stunner.core.definition.annotation.PropertySet;
import org.kie.workbench.common.stunner.core.definition.annotation.definition.Category;
import org.kie.workbench.common.stunner.core.definition.annotation.definition.Labels;
import org.kie.workbench.common.stunner.core.factory.graph.NodeFactory;
import org.kie.workbench.common.stunner.core.util.HashUtil;

import static org.kie.workbench.common.forms.adf.engine.shared.formGeneration.processing.fields.fieldInitializers.nestedForms.SubFormFieldInitializer.COLLAPSIBLE_CONTAINER;
import static org.kie.workbench.common.forms.adf.engine.shared.formGeneration.processing.fields.fieldInitializers.nestedForms.SubFormFieldInitializer.FIELD_CONTAINER_PARAM;

@Portable
@Bindable
@Definition(graphFactory = NodeFactory.class)
@FormDefinition(policy = FieldPolicy.ONLY_MARKED, startElement = "id", defaultFieldSettings = {@FieldParam(name = FIELD_CONTAINER_PARAM, value = COLLAPSIBLE_CONTAINER)})
public class Decision extends DRGElement implements HasExpression,
                                                    HasVariable,
                                                    DMNViewDefinition {

    @Category
    public static final transient String stunnerCategory = Categories.NODES;

    @Labels
    private final Set<String> stunnerLabels = new HashSet<String>() {{
        add("decision");
    }};

    @Property
    @FormField(afterElement = "name")
    @Valid
    protected Question question;

    @Property
    @FormField(afterElement = "question")
    @Valid
    protected AllowedAnswers allowedAnswers;

    @PropertySet
    @FormField(afterElement = "allowedAnswers")
    @Valid
    protected InformationItem variable;

    protected Expression expression;

    @PropertySet
    @FormField
    @Valid
    protected BackgroundSet backgroundSet;

    @PropertySet
    @FormField
    @Valid
    protected FontSet fontSet;

    @PropertySet
    @FormField
    @Valid
    protected RectangleDimensionsSet dimensionsSet;

    public Decision() {
        this(new Id(),
             new org.kie.workbench.common.dmn.api.property.dmn.Description(),
             new Name(),
             new Question(),
             new AllowedAnswers(),
             new InformationItem(),
             null,
             new BackgroundSet(),
             new FontSet(),
             new RectangleDimensionsSet());
    }

    public Decision(final Id id,
                    final org.kie.workbench.common.dmn.api.property.dmn.Description description,
                    final Name name,
                    final Question question,
                    final AllowedAnswers allowedAnswers,
                    final InformationItem variable,
                    final Expression expression,
                    final BackgroundSet backgroundSet,
                    final FontSet fontSet,
                    final RectangleDimensionsSet dimensionsSet) {
        super(id,
              description,
              name);
        this.question = question;
        this.allowedAnswers = allowedAnswers;
        this.variable = variable;
        this.expression = expression;
        this.backgroundSet = backgroundSet;
        this.fontSet = fontSet;
        this.dimensionsSet = dimensionsSet;
    }

    // -----------------------
    // Stunner core properties
    // -----------------------

    public String getStunnerCategory() {
        return stunnerCategory;
    }

    public Set<String> getStunnerLabels() {
        return stunnerLabels;
    }

    @Override
    public BackgroundSet getBackgroundSet() {
        return backgroundSet;
    }

    public void setBackgroundSet(final BackgroundSet backgroundSet) {
        this.backgroundSet = backgroundSet;
    }

    @Override
    public FontSet getFontSet() {
        return fontSet;
    }

    public void setFontSet(final FontSet fontSet) {
        this.fontSet = fontSet;
    }

    @Override
    public RectangleDimensionsSet getDimensionsSet() {
        return dimensionsSet;
    }

    public void setDimensionsSet(final RectangleDimensionsSet dimensionsSet) {
        this.dimensionsSet = dimensionsSet;
    }

    // -----------------------
    // DMN properties
    // -----------------------

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(final Question question) {
        this.question = question;
    }

    public AllowedAnswers getAllowedAnswers() {
        return allowedAnswers;
    }

    public void setAllowedAnswers(final AllowedAnswers allowedAnswers) {
        this.allowedAnswers = allowedAnswers;
    }

    @Override
    public InformationItem getVariable() {
        return variable;
    }

    @Override
    public void setVariable(final InformationItem variable) {
        this.variable = variable;
    }

    @Override
    public Expression getExpression() {
        return expression;
    }

    @Override
    public void setExpression(final Expression expression) {
        this.expression = expression;
    }

    @Override
    public DMNModelInstrumentedBase asDMNModelInstrumentedBase() {
        return this;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Decision)) {
            return false;
        }

        Decision that = (Decision) o;

        if (id != null ? !id.equals(that.id) : that.id != null) {
            return false;
        }
        if (description != null ? !description.equals(that.description) : that.description != null) {
            return false;
        }
        if (name != null ? !name.equals(that.name) : that.name != null) {
            return false;
        }
        if (question != null ? !question.equals(that.question) : that.question != null) {
            return false;
        }
        if (allowedAnswers != null ? !allowedAnswers.equals(that.allowedAnswers) : that.allowedAnswers != null) {
            return false;
        }
        if (variable != null ? !variable.equals(that.variable) : that.variable != null) {
            return false;
        }
        if (expression != null ? !expression.equals(that.expression) : that.expression != null) {
            return false;
        }
        if (backgroundSet != null ? !backgroundSet.equals(that.backgroundSet) : that.backgroundSet != null) {
            return false;
        }
        if (fontSet != null ? !fontSet.equals(that.fontSet) : that.fontSet != null) {
            return false;
        }
        return dimensionsSet != null ? dimensionsSet.equals(that.dimensionsSet) : that.dimensionsSet == null;
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(id != null ? id.hashCode() : 0,
                                         description != null ? description.hashCode() : 0,
                                         name != null ? name.hashCode() : 0,
                                         variable != null ? variable.hashCode() : 0,
                                         question != null ? question.hashCode() : 0,
                                         allowedAnswers != null ? allowedAnswers.hashCode() : 0,
                                         variable != null ? variable.hashCode() : 0,
                                         expression != null ? expression.hashCode() : 0,
                                         backgroundSet != null ? backgroundSet.hashCode() : 0,
                                         fontSet != null ? fontSet.hashCode() : 0,
                                         dimensionsSet != null ? dimensionsSet.hashCode() : 0);
    }
}
