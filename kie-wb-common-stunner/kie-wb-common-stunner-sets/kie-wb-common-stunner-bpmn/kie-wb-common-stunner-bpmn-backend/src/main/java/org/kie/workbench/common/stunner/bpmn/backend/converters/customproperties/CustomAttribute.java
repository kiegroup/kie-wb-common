package org.kie.workbench.common.stunner.bpmn.backend.converters.customproperties;

import java.util.Optional;

import org.eclipse.bpmn2.BaseElement;
import org.jboss.drools.DroolsPackage;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.Package;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;

public class CustomAttribute<T> {

    private static final String droolsns = DroolsPackage.eNS_URI;

    public static final AttributeDefinition<Boolean> independent = new BooleanAttribute(droolsns, "independent", false);
    public static final AttributeDefinition<Boolean> adHoc = new BooleanAttribute(droolsns, "adHoc", false);
    public static final AttributeDefinition<Boolean> waitForCompletion = new BooleanAttribute(droolsns, "waitForCompletion", false);
    public static final AttributeDefinition<String> ruleFlowGroup = new StringAttribute(droolsns, "ruleFlowGroup", "");
    public static final AttributeDefinition<String> packageName = new StringAttribute(droolsns, "packageName", Package.DEFAULT_PACKAGE);
    public static final AttributeDefinition<String> version = new StringAttribute(droolsns, "version", "1.0");
    public static final AttributeDefinition<String> errorName = new StringAttribute(droolsns, "erefname", "");
    public static final AttributeDefinition<Boolean> boundarycaForBoundaryEvent = new BooleanAttribute(droolsns, "boundaryca", false) {
        @Override
        public Boolean getValue(BaseElement element) {
            // this is for compatibility with legacy marshallers
            // always return the default regardless the string was empty in the file
            // or it was actually undefined
            String value = super.getStringValue(element).orElse("");
            return value.isEmpty() ? defaultValue : Boolean.parseBoolean(value);
        }
    };
    public static final AttributeDefinition<Boolean> boundarycaForEvent = new BooleanAttribute(droolsns, "boundaryca", true) {
        @Override
        public Boolean getValue(BaseElement element) {
            // this is for compatibility with legacy marshallers
            // always return the default regardless the string was empty in the file
            // or it was actually undefined
            String value = super.getStringValue(element).orElse("");
            return value.isEmpty() ? defaultValue : Boolean.parseBoolean(value);
        }
    };
    public static final AttributeDefinition<String> priority = new StringAttribute(droolsns, "priority", null);
    public static final AttributeDefinition<String> dtype = new StringAttribute(droolsns, "dtype", "");

    public static final AttributeDefinition<String> dg = new StringAttribute(droolsns, "dg", "") {
        @Override
        public String getValue(BaseElement element) {
            // this is for compatibility with legacy marshallers
            // always return null regardless the string was empty in the file
            // or it was actually undefined
            String value = super.getValue(element);
            return value.isEmpty() ? null : value;
        }
    };

    public static final AttributeDefinition<Point2D> dockerInfo = new AttributeDefinition<Point2D>(droolsns, "dockerinfo", Point2D.create(0, 0)) {
        @Override
        public Point2D getValue(BaseElement element) {
            Optional<String> attribute = getStringValue(element);

            if (attribute.isPresent()) {
                String dockerInfoStr = attribute.get();
                dockerInfoStr = dockerInfoStr.substring(0, dockerInfoStr.length() - 1);
                String[] dockerInfoParts = dockerInfoStr.split("\\|");
                String infoPartsToUse = dockerInfoParts[0];
                String[] infoPartsToUseParts = infoPartsToUse.split("\\^");

                double x = Double.valueOf(infoPartsToUseParts[0]);
                double y = Double.valueOf(infoPartsToUseParts[1]);

                return Point2D.create(x, y);
            } else {
                return Point2D.create(0, 0);
            }
        }

        @Override
        public void setValue(BaseElement element, Point2D value) {
            throw new UnsupportedOperationException("not yet implemented");
        }
    };

    private final AttributeDefinition<T> attributeDefinition;
    private final BaseElement element;

    public CustomAttribute(AttributeDefinition<T> attributeDefinition, BaseElement element) {
        this.attributeDefinition = attributeDefinition;
        this.element = element;
    }

    public T get() {
        return attributeDefinition.getValue(element);
    }

    public void set(T value) {
        attributeDefinition.setValue(element, value);
    }
}
