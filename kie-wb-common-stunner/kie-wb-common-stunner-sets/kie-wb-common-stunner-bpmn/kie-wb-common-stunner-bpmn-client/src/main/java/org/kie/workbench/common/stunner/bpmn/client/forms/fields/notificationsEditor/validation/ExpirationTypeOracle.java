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

    public static final String ISO_DATE_TIME = "(2[0-9][0-9]{2}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2})([:|+|-]([0-9]{2}:[0-9]{2}|[0-9]{2}|00Z))";
    public static final String REPEATABLE = "^R([0-9]*)";
    public static final String PERIOD = "P(T?)([1-9]\\d*)([MHDWYS])";
    public static final String ONE_TIME_EXECUTION = "^(\\d+)([mwhdyMHWDYS])$";

    public static final String TIME = "T([1-9]\\d*[H])?([1-9]\\d*[M])?([1-9]\\d*[S])?([1-9]\\d*MS)?$";
    public static final String DURATION = "P([1-9]\\d*[Y])?([1-9]\\d*[M])?([1-9]\\d*[W])?([1-9]\\d*[D])?";

    public static final String OLD_DURATION = "^([1-9]\\d*[mhdyMHWDYS])(([1-9]\\d*(m|M){1})?([1-9]\\d*[w|W])?([1-9]\\d*[d|D])?([1-9]\\d*[h|H])?([1-9]\\d*[m|M])?([1-9]\\d*[s|S])?([1-9]\\d*(ms|MS|mS))?)?$";

    public static final String REPEATING_INTERVALS_TIME = REPEATABLE + "/" + DURATION + TIME;
    public static final String REPEATING_INTERVALS = REPEATABLE + "/" + DURATION + "$";
    public static final String REPEATABLE_START_END = REPEATABLE + "/" + ISO_DATE_TIME + "/" + ISO_DATE_TIME + "$";
    public static final String REPEATABLE_DURATION_END = REPEATABLE + "/" + DURATION + "/" + ISO_DATE_TIME + "$";

    private Map<Expiration, List<String>> patterns = ImmutableMap.of(
            Expiration.TIMEPERIOD, Arrays.asList(REPEATABLE + "/" + PERIOD + "$", "^" + PERIOD + "$"),
            Expiration.DATETIME, Arrays.asList(REPEATABLE + "/" + ISO_DATE_TIME + "/" + PERIOD, "^" + ISO_DATE_TIME + "$"),
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
