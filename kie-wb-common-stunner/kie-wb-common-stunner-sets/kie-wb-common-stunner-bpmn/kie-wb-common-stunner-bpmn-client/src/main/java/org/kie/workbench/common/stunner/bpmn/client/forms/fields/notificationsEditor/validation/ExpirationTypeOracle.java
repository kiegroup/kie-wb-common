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

package org.kie.workbench.common.stunner.bpmn.client.forms.fields.notificationsEditor.validation;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.Expiration;

import static org.kie.workbench.common.stunner.bpmn.client.forms.fields.notificationsEditor.validation.NotificationValueValidator.checkIfPatternMatch;

public class ExpirationTypeOracle {

    public static final String ISO_DATA_TIME = "(2[0-9][0-9]{2}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2})([:|+|-]([0-9]{2}:[0-9]{2}|[0-9]{2}|00Z))";
    public static final String REPEATABLE = "^R([0-9]*)";
    public static final String PERIOD = "[P](T?)([0-9]*)([MHDY])";
    public static final String ONE_TIME_EXECUTION = "^(\\d+)([mhdyMHDY])";

    private Map<Expiration, List<String>> patterns = ImmutableMap.of(
            Expiration.TIMEPERIOD, Arrays.asList(REPEATABLE + "/" + PERIOD, "^" + PERIOD),
            Expiration.DATATIME, Arrays.asList(REPEATABLE + "/" + ISO_DATA_TIME + "/" + PERIOD, "^" + ISO_DATA_TIME),
            Expiration.EXPRESSION, Arrays.asList());

    public ExpirationTypeOracle() {

    }

    public Expiration guess(String maybeIso) {
        for (Map.Entry<Expiration, List<String>> value : patterns.entrySet()) {
            for (String pattern : value.getValue()) {
                if (checkIfPatternMatch.apply(pattern, maybeIso).isPresent()) {
                    return value.getKey();
                }
            }
        }
        return Expiration.EXPRESSION;
    }
}
