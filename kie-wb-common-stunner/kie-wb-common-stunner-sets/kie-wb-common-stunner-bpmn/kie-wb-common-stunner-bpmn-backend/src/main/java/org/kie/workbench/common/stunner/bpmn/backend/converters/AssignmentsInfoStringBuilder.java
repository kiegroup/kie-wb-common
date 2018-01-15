package org.kie.workbench.common.stunner.bpmn.backend.converters;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.bpmn2.Activity;
import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.DataInput;
import org.eclipse.bpmn2.DataInputAssociation;
import org.eclipse.bpmn2.DataOutput;
import org.eclipse.bpmn2.DataOutputAssociation;
import org.eclipse.bpmn2.InputSet;
import org.eclipse.bpmn2.ItemAwareElement;
import org.eclipse.bpmn2.OutputSet;
import org.eclipse.bpmn2.Property;
import org.eclipse.bpmn2.SubProcess;
import org.eclipse.emf.ecore.util.FeatureMap;

public class AssignmentsInfoStringBuilder {
    public static String makeString(
            final List<DataInput> datainput,
            final InputSet inputSets,
            final List<DataInputAssociation> inputAssociations,
            final List<DataOutput> dataoutput,
            final OutputSet dataoutputset,
            final List<DataOutputAssociation> outputAssociations) {

        String dataInputString = dataInputsToString(datainput);
        String inputSetsToString = toString(inputSets);
        String dataInputAssociationsToString = inAssociationsToString(inputAssociations);

        String dataOutputString = dataOutputsToString(dataoutput);
        String outputSetsToString = toString(dataoutputset);
        String dataOutputAssociationsToString = outAssociationsToString(outputAssociations);

        String associationString =
                dataInputAssociationsToString + dataOutputAssociationsToString;


        return Arrays.asList(dataInputString,
                             inputSetsToString,
                             dataOutputString,
                             outputSetsToString,
                             associationString
                             ).stream().collect(Collectors.joining("|"));

    }

    private static String outputSetsToString(List<OutputSet> dataoutputset) {
        return null;
    }

    public static String dataInputsToString(List<DataInput> dataInputs) {
        return dataInputs.stream()
                .map(AssignmentsInfoStringBuilder::toString)
                .collect(Collectors.joining(","));
    }

    public static String dataOutputsToString(List<DataOutput> dataInputs) {
        return dataInputs.stream()
                .map(AssignmentsInfoStringBuilder::toString)
                .collect(Collectors.joining(","));
    }


    private static String toString(OutputSet outputSet) {
        return "";
    }

    public static String toString(InputSet dataInput) {
        return "";
    }

    @Deprecated
    private void marshallItemAwareElements(Activity activity,
                                           List<? extends ItemAwareElement> elements,
                                           StringBuilder buffer,
                                           List<String> disallowedNames) {
        for (ItemAwareElement element : elements) {
            String name = null;
            if (element instanceof DataInput) {
                name = ((DataInput) element).getName();
            }
            if (element instanceof DataOutput) {
                name = ((DataOutput) element).getName();
            }
            if (name != null && !name.isEmpty() && !disallowedNames.contains(name)) {
                buffer.append(name);
                if (element.getItemSubjectRef() != null && element.getItemSubjectRef().getStructureRef() != null && !element.getItemSubjectRef().getStructureRef().isEmpty()) {
                    buffer.append(":").append(element.getItemSubjectRef().getStructureRef());
                } else if (activity.eContainer() instanceof SubProcess) {
                    // BZ1247105: for Outputs on Tasks inside sub-processes
                    String dtype = extractDtype(element);
                    if (dtype != null && !dtype.isEmpty()) {
                        buffer.append(":").append(dtype);
                    }
                }
                buffer.append(",");
            }
        }
    }

    public static String outAssociationsToString(List<DataOutputAssociation> outputAssociations) {
            StringBuffer doutassociationbuff = new StringBuffer();
            for (DataOutputAssociation doa : outputAssociations) {
                String doaName = ((DataOutput) doa.getSourceRef().get(0)).getName();
                if (doaName != null && doaName.length() > 0) {
                    doutassociationbuff.append("[dout]" + doaName);
                    doutassociationbuff.append("->");
                    doutassociationbuff.append(doa.getTargetRef().getId());
                    doutassociationbuff.append(",");
                }
            }
            if (doutassociationbuff.length() > 0) {
                doutassociationbuff.setLength(doutassociationbuff.length() - 1);
            }
            return doutassociationbuff.toString();
    }

    public static String inAssociationsToString(List<DataInputAssociation> inputAssociations) {
        StringBuffer doutassociationbuff = new StringBuffer();
        for (DataInputAssociation dia : inputAssociations) {
            String doaName = dia.getSourceRef().get(0).getId();
            if (doaName != null && doaName.length() > 0) {
                doutassociationbuff.append("[din]" + doaName);
                doutassociationbuff.append("->");
                doutassociationbuff.append(((DataInput)dia.getTargetRef()).getName());
                doutassociationbuff.append(",");
            }
        }
        if (doutassociationbuff.length() > 0) {
            doutassociationbuff.setLength(doutassociationbuff.length() - 1);
        }
        return doutassociationbuff.toString();
    }


    public static String toString(DataInput dataInput) {
        String name = dataInput.getName();
        String dtype = extractDtype(dataInput);
        return dtype.isEmpty() ? name : name + ':' + dtype;
    }

    public static String toString(DataOutput dataInput) {
        String name = dataInput.getName();
        String dtype = extractDtype(dataInput);
        return dtype.isEmpty() ? name : name + ':' + dtype;
    }



    private static String extractDtype(BaseElement el) {
        return getAnyAttributeValue(el, "dtype"); // fixme: look for a safer way to do this
    }

    static String getAnyAttributeValue(BaseElement el, String attrName) {
        for (FeatureMap.Entry entry : el.getAnyAttribute()) {
            if (attrName.equals(entry.getEStructuralFeature().getName())) {
                return entry.getValue().toString();
            }
        }
        return "";
    }

    @Deprecated
    private String setAssignmentsInfoProperty(
            final String datainput,
            final String datainputset,
            final String dataoutput,
            final String dataoutputset,
            final String assignments) {

        return Arrays.asList(datainput,
                      datainputset,
                      dataoutput,
                      dataoutputset,
                      assignments).stream().collect(Collectors.joining("|"));

    }
}
