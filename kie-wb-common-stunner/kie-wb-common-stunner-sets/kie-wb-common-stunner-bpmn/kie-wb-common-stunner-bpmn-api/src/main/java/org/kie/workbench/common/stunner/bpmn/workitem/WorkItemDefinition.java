/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.workitem;

import org.guvnor.common.services.project.model.Dependencies;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.stunner.core.util.HashUtil;

@Portable
public class WorkItemDefinition {

    private String uri;
    private String name;
    private String description;
    private String category;
    private String displayName;
    private String documentation;
    private IconDefinition iconDefinition;
    private String defaultHandler;
    private String parameters;
    private String results;
    private Dependencies dependencies;

    public void setUri(String uri) {
        this.uri = uri;
    }

    public WorkItemDefinition setName(String name) {
        this.name = name;
        return this;
    }

    public WorkItemDefinition setDescription(String description) {
        this.description = description;
        return this;
    }

    public WorkItemDefinition setCategory(String category) {
        this.category = category;
        return this;
    }

    public WorkItemDefinition setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public WorkItemDefinition setDocumentation(String documentation) {
        this.documentation = documentation;
        return this;
    }

    public WorkItemDefinition setIconDefinition(IconDefinition iconDefinition) {
        this.iconDefinition = iconDefinition;
        return this;
    }

    public WorkItemDefinition setDefaultHandler(String defaultHandler) {
        this.defaultHandler = defaultHandler;
        return this;
    }

    public WorkItemDefinition setParameters(String parameters) {
        this.parameters = parameters;
        return this;
    }

    public WorkItemDefinition setResults(String results) {
        this.results = results;
        return this;
    }

    public WorkItemDefinition setDependencies(Dependencies dependencies) {
        this.dependencies = dependencies;
        return this;
    }

    public String getUri() {
        return uri;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDocumentation() {
        return documentation;
    }

    public IconDefinition getIconDefinition() {
        return iconDefinition;
    }

    public String getDefaultHandler() {
        return defaultHandler;
    }

    public String getParameters() {
        return parameters;
    }

    public String getResults() {
        return results;
    }

    public Dependencies getDependencies() {
        return dependencies;
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(super.hashCode(),
                                         name.hashCode(),
                                         (null != category) ? category.hashCode() : 0,
                                         (null != displayName) ? displayName.hashCode() : 0,
                                         (null != description) ? description.hashCode() : 0,
                                         (null != documentation) ? documentation.hashCode() : 0,
                                         (null != iconDefinition) ? iconDefinition.hashCode() : 0,
                                         (null != defaultHandler) ? defaultHandler.hashCode() : 0,
                                         (null != parameters) ? parameters.hashCode() : 0,
                                         (null != results) ? results.hashCode() : 0,
                                         (null != dependencies) ? dependencies.hashCode() : 0);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof WorkItemDefinition) {
            WorkItemDefinition other = (WorkItemDefinition) o;
            return super.equals(other) &&
                    name.equals(other.name) &&
                    ((null != category) ? category.equals(other.category) : null == other.category) &&
                    ((null != description) ? description.equals(other.description) : null == other.description) &&
                    ((null != displayName) ? displayName.equals(other.displayName) : null == other.displayName) &&
                    ((null != documentation) ? documentation.equals(other.documentation) : null == other.documentation) &&
                    ((null != iconDefinition) ? iconDefinition.equals(other.iconDefinition) : null == other.iconDefinition) &&
                    ((null != defaultHandler) ? defaultHandler.equals(other.defaultHandler) : null == other.defaultHandler) &&
                    ((null != parameters) ? parameters.equals(other.parameters) : null == other.parameters) &&
                    ((null != results) ? results.equals(other.results) : null == other.results) &&
                    ((null != dependencies) ? dependencies.equals(other.dependencies) : null == other.dependencies);
        }
        return false;
    }
}