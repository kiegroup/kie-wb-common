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

package org.kie.workbench.common.dmn.client.editors.types.listview.constraint.range;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import elemental2.dom.Element;
import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.DataTypeConstraintModal;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.DataTypeConstraintComponent;
import org.kie.workbench.common.stunner.core.util.StringUtils;
import org.uberfire.client.mvp.UberElemental;

@Dependent
public class DataTypeConstraintRange implements DataTypeConstraintComponent {

    private final View view;

    private DataTypeConstraintModal modal;

    @Inject
    public DataTypeConstraintRange(final View view) {
        this.view = view;
    }

    @PostConstruct
    void setup() {
        view.init(this);
    }

    @Override
    public String getValue() {
        return getRawValue();
    }

    @Override
    public void setValue(final String value) {
        final RangeValue rangeValue = getRangeValue(value);
        view.setIncludeStartValue(rangeValue.getIncludeStartValue());
        view.setStartValue(rangeValue.getStartValue());
        view.setEndValue(rangeValue.getEndValue());
        view.setIncludeEndValue(rangeValue.getIncludeEndValue());
    }

    private RangeValue getRangeValue(String value) {
        final RangeValue rangeValue = new RangeValue();

        value = value.trim();

        if (StringUtils.isEmpty(value)) {
            return rangeValue;
        }

        final String[] values = value.split("(\\.\\.)");
        if (values.length != 2
            || values[0].isEmpty()
            || values[1].isEmpty()) {
            return rangeValue;
        }

        final String startPart = values[0].replace("[", "").replace("(", "");
        rangeValue.setStartValue(startPart);

        final String endPart = values[1].replace("]", "").replace(")", "");
        rangeValue.setEndValue(endPart);

        rangeValue.setIncludeStartValue(value.charAt(0) == '[');
        rangeValue.setIncludeEndValue(value.charAt(value.length() - 1) == ']');
        return rangeValue;
    }

    @Override
    public Element getElement() {
        return view.getElement();
    }

    private String getRawValue() {
        final StringBuilder builder = new StringBuilder();
        if (view.getIncludeStartValue()) {
            builder.append("[");
        } else {
            builder.append("(");
        }

        builder.append(view.getStartValue());
        builder.append("..");
        builder.append(view.getEndValue());

        if (view.getIncludeEndValue()) {
            builder.append("]");
        } else {
            builder.append(")");
        }

        return builder.toString();
    }

    public void disableOkButton() {
        modal.disableOkButton();
    }

    public void enableOkButton() {
        modal.enableOkButton();
    }

    public void setModal(final DataTypeConstraintModal modal) {
        this.modal = modal;
        disableOkButton();
    }

    private class RangeValue {

        private boolean includeStartValue;
        private String startValue;
        private String endValue;
        private boolean includeEndValue;

        RangeValue() {
            this.startValue = "";
            this.endValue = "";
        }

        boolean getIncludeStartValue() {
            return includeStartValue;
        }

        String getStartValue() {
            return startValue;
        }

        String getEndValue() {
            return endValue;
        }

        boolean getIncludeEndValue() {
            return includeEndValue;
        }

        void setIncludeStartValue(final boolean includeStartValue) {
            this.includeStartValue = includeStartValue;
        }

        void setIncludeEndValue(final boolean includeEndValue) {
            this.includeEndValue = includeEndValue;
        }

        void setStartValue(final String startValue) {
            this.startValue = startValue;
        }

        void setEndValue(final String endValue) {
            this.endValue = endValue;
        }
    }

    public interface View extends UberElemental<DataTypeConstraintRange>,
                                  IsElement {

        String getStartValue();

        String getEndValue();

        void setStartValue(final String value);

        void setEndValue(final String value);

        boolean getIncludeStartValue();

        void setIncludeStartValue(final boolean includeStartValue);

        boolean getIncludeEndValue();

        void setIncludeEndValue(final boolean includeEndValue);
    }
}
