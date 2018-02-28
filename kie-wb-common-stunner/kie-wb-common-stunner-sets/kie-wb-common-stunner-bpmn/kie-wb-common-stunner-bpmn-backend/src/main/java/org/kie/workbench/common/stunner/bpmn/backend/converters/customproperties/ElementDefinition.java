package org.kie.workbench.common.stunner.bpmn.backend.converters.customproperties;

import java.util.Optional;

import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.Bpmn2Factory;
import org.eclipse.bpmn2.ExtensionAttributeValue;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.impl.EStructuralFeatureImpl;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.jboss.drools.DroolsFactory;
import org.jboss.drools.DroolsPackage;
import org.jboss.drools.MetaDataType;
import org.kie.workbench.common.stunner.bpmn.backend.legacy.util.Utils;

public abstract class ElementDefinition<T> {

    private final String name;
    protected final T defaultValue;

    public ElementDefinition(String name, T defaultValue) {
        this.name = name;
        this.defaultValue = defaultValue;
    }

    public String name() {
        return name;
    }

    public abstract T getValue(BaseElement element);

    public abstract void setValue(BaseElement element, T value);

    Optional<java.lang.String> getStringValue(BaseElement element) {
        return Optional.ofNullable(Utils.getMetaDataValue(element.getExtensionValues(), name));
    }

    void setStringValue(BaseElement element, String value) {
        if (element != null) {
            MetaDataType eleMetadata = DroolsFactory.eINSTANCE.createMetaDataType();
            eleMetadata.setName(name);
            eleMetadata.setMetaValue(asCData(value));

            if (element.getExtensionValues() == null || element.getExtensionValues().isEmpty()) {
                ExtensionAttributeValue extensionElement = Bpmn2Factory.eINSTANCE.createExtensionAttributeValue();
                element.getExtensionValues().add(extensionElement);
            }

            FeatureMap.Entry eleExtensionElementEntry = new EStructuralFeatureImpl.SimpleFeatureMapEntry(
                    (EStructuralFeature.Internal) DroolsPackage.Literals.DOCUMENT_ROOT__META_DATA,
                    eleMetadata);
            element.getExtensionValues().get(0).getValue().add(eleExtensionElementEntry);
        }
    }

    public CustomElement<T> of(BaseElement element) {
        return new CustomElement<>(this, element);
    }

    // eww
    protected String asCData(String original) {
        return "<![CDATA[" + original + "]]>";
    }
}

class BooleanElement extends ElementDefinition<Boolean> {

    BooleanElement(String name, java.lang.Boolean defaultValue) {
        super(name, defaultValue);
    }

    @Override
    public java.lang.Boolean getValue(BaseElement element) {
        return getStringValue(element)
                .map(java.lang.Boolean::parseBoolean)
                .orElse(defaultValue);
    }

    @Override
    public void setValue(BaseElement element, Boolean value) {
        setStringValue(element, String.valueOf(value));
    }
}

class StringElement extends ElementDefinition<String> {

    StringElement(String name, java.lang.String defaultValue) {
        super(name, defaultValue);
    }

    @Override
    public java.lang.String getValue(BaseElement element) {
        return getStringValue(element)
                .orElse(defaultValue);
    }

    @Override
    public void setValue(BaseElement element, String value) {
        setStringValue(element, value);
    }
}