package org.kie.workbench.common.forms.integration.tests.valueprocessing;

import static org.kie.workbench.common.forms.integration.tests.valueprocessing.TestUtils.createDate;

public enum SubformFields implements FormFields{

    CHECKBOX_BINDING("checkbox", true, false, true, true),
    TEXBOX_BINDING("textbox", "Joseph", "John", "Martin", "Joe"),
    TEXTAREA_BINDING("textarea", "Hello\n my\n name\n is Joseph\n", "Hello\n my\n name\n is John\n", "Hello\n my\n name\n is Martin\n", "This\n is\n not\n a joke!\n"),
    INTEGERBOX_BINDING("integerbox", 15, 100, 520, 2),
    DECIMALBOX_BINDING("decimalbox", 1.564, 40.5684, 20.1569, 3.14),
    DATEPICKER_BINDING("datepicker", createDate("06/06/1989"), createDate("17/11/1989"), createDate("11/09/2011"), createDate("06/06/1989")),
    SLIDER_BINDING("slider", 10.0, 26.0, 49.0, 5.0),
    LISTBOX_BINDING("listbox", "2", "2", "3", "2"),
    RADIOGROUP_BINDING("radiogroup", "one", "two", "three", "two");

    private final String binding;
    private final Object
            firstLineValue,
            secondLineValue,
            thirdLineValue,
            subformValue;

    public String getBinding() {
        return binding;
    }

    public Object getFirstLineValue() {
        return firstLineValue;
    }

    public Object getSecondLineValue() {
        return secondLineValue;
    }

    public Object getThirdLineValue() {
        return thirdLineValue;
    }

    public Object getSubformValue() {
        return subformValue;
    }

    SubformFields(String label, Object firstLineValue, Object secondLineValue, Object thirdLineValue, Object subformValue) {
        this.binding = label;
        this.firstLineValue = firstLineValue;
        this.secondLineValue = secondLineValue;
        this.thirdLineValue = thirdLineValue;
        this.subformValue = subformValue;
    }
}
