/*
 * Copyright 2024 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.library.api.preferences;

import org.uberfire.preferences.shared.impl.validation.StringPropertyValidator;

public class DescriptionLengthValidator extends StringPropertyValidator {

    private static final int DESCRIPTION_MAX_LENGTH = 3000;

    public DescriptionLengthValidator() {
        super(DescriptionLengthValidator::lengthCheck,
                "PropertyValidator.ConstrainedValuesValidator.NotAllowed");
    }

    private static boolean lengthCheck(final String description) {
        return description == null || description.length() <= DESCRIPTION_MAX_LENGTH;
    }
}
