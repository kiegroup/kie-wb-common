package org.kie.workbench.common.forms.integration.tests.valueprocessing;

public enum FormEngineFields implements FormFields{
    FORM_ENGINE_EDITED_OBJECT("__FormEngine-EditedObject", false, false, false),
    FORM_ENGINE_OBJECT_INDEX("__FormEngine-ObjectIndex", 0, 1, 2);

    private final String binding;
    private final Object
            firstLineValue,
            secondLineValue,
            thirdLineValue;

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
        throw new UnsupportedOperationException("FormEngineFields are not available for Subform.");
    }

    FormEngineFields(String label, Object firstLineValue, Object secondLineValue, Object thirdLineValue) {
        this.binding = label;
        this.firstLineValue = firstLineValue;
        this.secondLineValue = secondLineValue;
        this.thirdLineValue = thirdLineValue;
    }
}
