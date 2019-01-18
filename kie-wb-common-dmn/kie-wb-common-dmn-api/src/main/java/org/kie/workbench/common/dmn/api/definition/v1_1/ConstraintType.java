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

package org.kie.workbench.common.dmn.api.definition.v1_1;

import javax.xml.namespace.QName;

public enum ConstraintType {
    ENUMERATION("enumeration"),
    EXPRESSION("expression"),
    RANGE("range");

    private final String value;

    public static final QName KEY = new QName(DMNModelInstrumentedBase.Namespace.KIE.getUri(),
                                              "constraintType",
                                              DMNModelInstrumentedBase.Namespace.KIE.getPrefix());

    public static ConstraintType fromString(final String value) {

        if (ENUMERATION.value.equals(value)) {
            return ENUMERATION;
        }

        if (EXPRESSION.value.equals(value)) {
            return EXPRESSION;
        }

        if (RANGE.value.equals(value)) {
            return RANGE;
        }

        return null;
    }

    ConstraintType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }
}