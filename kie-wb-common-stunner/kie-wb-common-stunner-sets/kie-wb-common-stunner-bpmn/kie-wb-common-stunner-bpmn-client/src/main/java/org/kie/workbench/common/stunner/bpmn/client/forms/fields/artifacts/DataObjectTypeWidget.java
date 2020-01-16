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

package org.kie.workbench.common.stunner.bpmn.client.forms.fields.artifacts;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasValue;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.artifacts.widgets.DataObjectTypeSelect;
import org.kie.workbench.common.stunner.bpmn.definition.property.artifacts.DataObjectTypeValue;

@Dependent
@Templated
public class DataObjectTypeWidget extends Composite implements HasValue<DataObjectTypeValue> {

    @Inject
    @DataField
    private DataObjectTypeSelect select;

    private DataObjectTypeValue current;

    protected ValueChangeHandler handler;

    @Inject
    public DataObjectTypeWidget() {
    }

    @PostConstruct
    public void init() {
        select.setValue(Object.class.getSimpleName());

        handler = (event -> {
            DataObjectTypeValue value = new DataObjectTypeValue();
            value.setType(select.getValue());
            setValue(value, true);
        });

        select.addValueChangeHandler(handler);
    }

    @Override
    public DataObjectTypeValue getValue() {
        return current;
    }

    @Override
    public void setValue(DataObjectTypeValue value) {
        setValue(value, false);
    }

    @Override
    public void setValue(DataObjectTypeValue value, boolean fireEvents) {

        if (value != null) {
            DataObjectTypeValue oldValue = current;
            current = value;

            if (fireEvents) {
                ValueChangeEvent.fireIfNotEqual(this,
                                                oldValue,
                                                current);
            } else {
                select.setValue(value.getType());
            }
        }
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<DataObjectTypeValue> handler) {
        return addHandler(handler,
                          ValueChangeEvent.getType());
    }

    public void setReadOnly(boolean readOnly) {
        select.setReadOnly(readOnly);
    }
}
