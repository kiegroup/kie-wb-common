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

package org.kie.workbench.common.stunner.bpmn.client.forms.widgets;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.extras.select.client.ui.Option;
import org.gwtbootstrap3.extras.select.client.ui.Select;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Dependent
@Templated
public class TimeZonePicker extends Composite implements IsWidget,
                                                         HasValue<String> {

    @DataField
    protected Select tzSelect = new Select();
    protected HandlerManager handlerManager = createHandlerManager();
    protected List<TimeZoneDTO> zones = new ArrayList<>();
    //protected Set<Double> timezones = new TreeSet<>();
    @DataField
    Button tzSwitch = new Button();
    String current;
    TzSelectType tzSelectType = TzSelectType.TZ;
    double defaultOffset = new Date().getTimezoneOffset();

    public static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
        Map<Object, Boolean> map = new ConcurrentHashMap<>();
        return t -> map.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    @PostConstruct
    public void init() {

        JSONObject obj = JSONParser.parseStrict(TimeZone.INSTANCE.asJson().getText()).isObject();

        obj.keySet().forEach(key -> {
            zones.add(new TimeZoneDTO(key, obj.get(key).isObject()));
        });

        double offset = new Date().getTimezoneOffset();
        GWT.log("Offset " + offset);

        populateTzSelector();

        tzSwitch.setIcon(IconType.GLOBE);
        tzSwitch.setColor("blue");
        tzSwitch.addClickHandler(event -> {
            changeSwitchType();
        });

        tzSelect.addValueChangeHandler(event -> GWT.log("on change " + event.getValue()));
    }

    private void changeSwitchType() {
        if (tzSelectType.equals(TzSelectType.COUNTRY)) {
            tzSelectType = TzSelectType.TZ;
        } else {
            tzSelectType = TzSelectType.COUNTRY;
        }
        populateTzSelector();
    }

    @Override
    public String getValue() {
        GWT.log(" getValue " + tzSelect.getSelectedItem().getValue());
        return tzSelect.getSelectedItem().getValue();
    }

    @Override
    public void setValue(String value) {
        parse(value);
        setValue(value, false);
    }

    @Override
    public void setValue(String value, boolean fireEvents) {
        String oldValue = current;
        current = value;
        if (fireEvents) {
            ValueChangeEvent.fireIfNotEqual(this, oldValue, value);
        }
    }

    private void parse(String value) {
        if (value != null && value.length() >= 2) {

        }
    }

    void populateTzSelector() {
        tzSelect.clear();

        if (tzSelectType.equals(TzSelectType.COUNTRY)) {
            for (int i = 0; i < zones.size(); i++) {
                Option option = new Option();
                option.setText(zones.get(i).name);
                option.setValue(zones.get(i).offsetAsString + "");
                if (new Double(zones.get(i).offsetAsDouble).equals(new Double(defaultOffset / 60))) {
                    option.setSelected(true);
                }
                tzSelect.add(option);
            }
        } else {
            zones.stream().filter(distinctByKey(p -> p.offsetAsDouble)).sorted((z1, z2) -> Double.valueOf(z1.offsetAsDouble).compareTo(new Double(z2.offsetAsDouble)))
                    .collect(Collectors.toCollection(LinkedHashSet::new))
                    .forEach(zone -> {
                        Option option = new Option();
                        option.setText(zone.offsetAsString + "");
                        option.setValue(zone.offsetAsString + "");
                        if (new Double(zone.offsetAsDouble).equals(new Double(defaultOffset / 60))) {
                            option.setSelected(true);
                        }
                        tzSelect.add(option);
                    });
        }

        tzSelect.refresh();
    }

    @Override
    public Widget asWidget() {
        return this;
    }

    @Override
    public void fireEvent(GwtEvent<?> event) {
        if (handlerManager != null) {
            handlerManager.fireEvent(event);
        }
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<String> handler) {
        return handlerManager.addHandler(ValueChangeEvent.getType(), handler);
    }

    enum TzSelectType {
        TZ,
        COUNTRY
    }

    interface TimeZone extends ClientBundle {

        TimeZone INSTANCE = GWT.create(TimeZone.class);

        @Source("timezones.json")
        TextResource asJson();
    }

    static class TimeZoneDTO {

        String name;
        String code;
        double offsetAsDouble;
        String offsetAsString;

        TimeZoneDTO(String code, JSONObject json) {

            this(json.get("name").isString().stringValue(),
                 json.get("offset").isString().stringValue(),
                 Double.parseDouble(json.get("offset")
                                            .isString()
                                            .stringValue()
                                            .replace(":", ".")));
            this.code = code;
        }

        private TimeZoneDTO(String name, String offsetAsString, double offsetAsDouble) {
            this.name = name;
            this.offsetAsString = offsetAsString;
            this.offsetAsDouble = offsetAsDouble;
        }

        @Override
        public String toString() {
            return "TimeZoneDTO{" +
                    "code='" + code + '\'' +
                    ", name='" + name + '\'' +
                    ", offsetAsString=" + offsetAsString +
                    ", offsetAsDouble=" + offsetAsDouble +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof TimeZoneDTO)) {
                return false;
            }
            TimeZoneDTO that = (TimeZoneDTO) o;
            return offsetAsString == that.offsetAsString &&
                    Objects.equals(name, that.name) &&
                    Objects.equals(code, that.code);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, code, offsetAsDouble);
        }
    }
}
