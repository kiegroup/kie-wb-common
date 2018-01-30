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

package org.kie.workbench.common.stunner.bpmn.backend.converters.properties;

import org.eclipse.bpmn2.BaseElement;
import org.kie.workbench.common.stunner.bpmn.backend.legacy.util.Utils;

public class AbstractPropertyReader {

    protected final BaseElement element;

    public AbstractPropertyReader(BaseElement element) {
        this.element = element;
    }

    public String getDocumentation() {
        return element.getDocumentation().stream()
                .findFirst()
                .map(org.eclipse.bpmn2.Documentation::getText)
                .orElse("");
    }

    public String getDescription() {
        return metaData("customDescription");
    }

    protected String attribute(String attributeId) {
        return Properties.findAnyAttribute(element, attributeId);
    }

    protected String metaData(String name) {
        return Utils.getMetaDataValue(element.getExtensionValues(), name);
    }
}
