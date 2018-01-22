package org.kie.workbench.common.stunner.bpmn.backend.converters.properties;

import org.eclipse.bpmn2.BaseElement;

public class ElementPropertyReader<T extends BaseElement> {
    protected final T element;

    public ElementPropertyReader(T element) {
        this.element = element;
    }

    public String getSignalScope() {
       return Properties.findMetaValue(element.getExtensionValues(), "customScope");
    }

    protected boolean findMetaBoolean(String name) {
        return Properties.findMetaBoolean(element.getExtensionValues(), name);
    }

    protected String findMetaValue(String name) {
        return Properties.findMetaValue(element.getExtensionValues(), name);
    }

}
