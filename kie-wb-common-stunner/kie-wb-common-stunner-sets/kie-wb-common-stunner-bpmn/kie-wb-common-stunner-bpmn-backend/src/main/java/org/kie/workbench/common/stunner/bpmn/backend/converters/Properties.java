package org.kie.workbench.common.stunner.bpmn.backend.converters;

import java.util.List;
import java.util.Optional;

import org.eclipse.bpmn2.Assignment;
import org.eclipse.bpmn2.DataInput;
import org.eclipse.bpmn2.DataInputAssociation;
import org.eclipse.bpmn2.ExtensionAttributeValue;
import org.eclipse.bpmn2.FormalExpression;
import org.kie.workbench.common.stunner.bpmn.backend.legacy.util.Utils;

public class Properties {
    public static boolean findMetaBoolean(List<ExtensionAttributeValue> extensions, String name) {
        return Boolean.parseBoolean(findMetaValue(extensions, name));
    }

    public static String findMetaValue(List<ExtensionAttributeValue> extensions, String name) {
        return Utils.getMetaDataValue(extensions, name);
    }

    public static boolean findInputBooleans(List<DataInputAssociation> associations, String name) {
        return Boolean.parseBoolean(findInputValue(associations, name));
    }

    public static String findInputValue(List<DataInputAssociation> associations, String name) {
        for (DataInputAssociation din : associations) {
            DataInput targetRef = (DataInput) (din.getTargetRef());
            if (targetRef.getName().equalsIgnoreCase(name)) {
                Assignment assignment = din.getAssignment().get(0);
                return evaluate(assignment).toString();
            }
        }
        return "";
    }

    private static Object evaluate(Assignment assignment) {
        return ((FormalExpression) assignment.getFrom()).getMixed().getValue(0);
    }

    private static Optional<DataInput> findByName(List<DataInput> dataInputs, String name) {
        return dataInputs.stream().filter(in -> in.getName().equals(name)).findFirst();
    }
}
