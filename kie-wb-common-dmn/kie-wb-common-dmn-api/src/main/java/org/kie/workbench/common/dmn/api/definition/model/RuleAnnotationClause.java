/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.api.definition.model;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.property.dmn.Name;

@Portable
public class RuleAnnotationClause extends DMNElement implements HasName {

    private Name name;

    public RuleAnnotationClause() {
        this.name = new Name();
    }

    @Override
    public Name getName() {
        return this.name;
    }

    @Override
    public void setName(final Name name) {
        this.name = name;
    }
}
