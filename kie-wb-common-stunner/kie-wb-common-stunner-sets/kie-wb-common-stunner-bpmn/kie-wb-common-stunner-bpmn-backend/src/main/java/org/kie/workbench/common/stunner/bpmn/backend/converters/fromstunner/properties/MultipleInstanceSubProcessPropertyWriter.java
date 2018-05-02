package org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties;

import java.util.NoSuchElementException;

import org.eclipse.bpmn2.Bpmn2Factory;
import org.eclipse.bpmn2.DataInput;
import org.eclipse.bpmn2.DataInputAssociation;
import org.eclipse.bpmn2.DataOutput;
import org.eclipse.bpmn2.DataOutputAssociation;
import org.eclipse.bpmn2.FormalExpression;
import org.eclipse.bpmn2.InputOutputSpecification;
import org.eclipse.bpmn2.InputSet;
import org.eclipse.bpmn2.ItemAwareElement;
import org.eclipse.bpmn2.ItemDefinition;
import org.eclipse.bpmn2.MultiInstanceLoopCharacteristics;
import org.eclipse.bpmn2.OutputSet;
import org.eclipse.bpmn2.Property;
import org.eclipse.bpmn2.SubProcess;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.Ids;

import static org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.Factories.bpmn2;

public class MultipleInstanceSubProcessPropertyWriter extends SubProcessPropertyWriter {

    private final MultiInstanceLoopCharacteristics miloop;
    private final InputOutputSpecification ioSpec;

    public MultipleInstanceSubProcessPropertyWriter(SubProcess process, VariableScope variableScope) {
        super(process, variableScope);
        this.miloop = bpmn2.createMultiInstanceLoopCharacteristics();
        process.setLoopCharacteristics(miloop);
        this.ioSpec = bpmn2.createInputOutputSpecification();
        process.setIoSpecification(ioSpec);
    }

    public void setInput(String collectionInput, String dataInput) {
        DataInput dataInputElement = setDataInput(dataInput);
        Property prop = findPropertyById(collectionInput);
        miloop.setLoopDataInputRef(prop);

        DataInputAssociation dia = Bpmn2Factory.eINSTANCE.createDataInputAssociation();
        ItemAwareElement ie = Bpmn2Factory.eINSTANCE.createItemAwareElement();
        ie.setId(collectionInput);
        dia.getSourceRef().add(ie);
        dia.setTargetRef(dataInputElement);
        process.getDataInputAssociations().add(dia);
    }

    public void setOutput(String collectionOutput, String dataOutput) {
        DataOutput dataOutputElement = setDataOutput(dataOutput);
        Property prop = findPropertyById(collectionOutput);
        miloop.setLoopDataOutputRef(prop);

        DataOutputAssociation doa = Bpmn2Factory.eINSTANCE.createDataOutputAssociation();
        ItemAwareElement ie = Bpmn2Factory.eINSTANCE.createItemAwareElement();
        ie.setId(collectionOutput);
        doa.getSourceRef().add(dataOutputElement);
        doa.setTargetRef(ie);
        process.getDataOutputAssociations().add(doa);
    }

    private Property findPropertyById(String id) {
        VariableScope.Variable lookup = variableScope.lookup(id);
        if (lookup == null) {
            throw new NoSuchElementException("Cannot find property with id " + id);
        } else {
            return lookup.getTypedIdentifier();
        }
    }

    public DataInput setDataInput(String value) {
        DataInput dataInput = bpmn2.createDataInput();
        dataInput.setId(value);
        this.ioSpec.getDataInputs().add(dataInput);
        InputSet inputSet = bpmn2.createInputSet();
        this.ioSpec.getInputSets().add(inputSet);
        inputSet.getDataInputRefs().add(dataInput);
        this.miloop.setLoopDataInputRef(dataInput);
        return dataInput;
    }

    public DataOutput setDataOutput(String value) {
        DataOutput dataOutput = bpmn2.createDataOutput();
        dataOutput.setId(value);
        ItemDefinition item = bpmn2.createItemDefinition();
        item.setId(Ids.multiInstanceItemType(process.getId()));
        this.addItemDefinition(item);
        dataOutput.setItemSubjectRef(item);
        this.ioSpec.getDataOutputs().add(dataOutput);
        OutputSet outputSet = bpmn2.createOutputSet();
        this.ioSpec.getOutputSets().add(outputSet);
        outputSet.getDataOutputRefs().add(dataOutput);
        this.miloop.setLoopDataOutputRef(dataOutput);
        return dataOutput;
    }

    public void setTrigger(String miTrigger) {

    }

    public void setCompletionCondition(String expression) {
        FormalExpression formalExpression = bpmn2.createFormalExpression();
        formalExpression.setBody(expression);
        this.miloop.setCompletionCondition(formalExpression);

        // BLAHHH
        this.miloop.setInputDataItem(
                process.getIoSpecification().getDataInputs().get(0));
        this.miloop.setOutputDataItem(
                process.getIoSpecification().getDataOutputs().get(0));
    }
}
