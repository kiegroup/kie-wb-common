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
package org.kie.workbench.common.dmn.webapp.kogito.common.client.services;

import java.util.Objects;

import org.kie.workbench.common.dmn.api.editors.types.RangeValue;
import org.kie.workbench.common.stunner.core.util.StringUtils;

public class FEELRangeParser {

    private static final String INCLUDE_START = "[";
    private static final String EXCLUDE_START = "(";
    private static final String INCLUDE_END = "]";
    private static final String EXCLUDE_END = ")";

    public static RangeValue parse(final String input) {
        final RangeValue rangeValue = new RangeValue();
        if (Objects.isNull(input)) {
            return rangeValue;
        }
        final String _input = input.trim();
        if (!(_input.startsWith(INCLUDE_START) || _input.startsWith(EXCLUDE_START))) {
            return rangeValue;
        }
        if (!(_input.endsWith(INCLUDE_END) || _input.endsWith(EXCLUDE_END))) {
            return rangeValue;
        }

        boolean inQuotes = false;
        boolean includeStartValue = _input.startsWith(INCLUDE_START);
        boolean includeEndValue = _input.endsWith(INCLUDE_END);
        String startValue = "";
        String endValue = "";

        for (int current = 0; current < _input.length(); current++) {
            if (_input.charAt(current) == '\"') {
                inQuotes = !inQuotes;
            }

            if (!inQuotes && _input.substring(current, current + 2).equals("..")) {
                startValue = _input.substring(1, current).trim();
                endValue = _input.substring(current + 2, _input.length() - 1).trim();
                break;
            }
        }

        if (StringUtils.isEmpty(startValue) || StringUtils.isEmpty(endValue)) {
            return rangeValue;
        }

        rangeValue.setIncludeStartValue(includeStartValue);
        rangeValue.setStartValue(startValue);
        rangeValue.setIncludeEndValue(includeEndValue);
        rangeValue.setEndValue(endValue);

        return rangeValue;
    }
}
