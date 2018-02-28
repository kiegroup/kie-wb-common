package org.kie.workbench.common.stunner.bpmn.backend.fromstunner.properties;

import org.eclipse.bpmn2.DataOutput;
import org.eclipse.bpmn2.DataOutputAssociation;
import org.eclipse.bpmn2.ItemDefinition;
import org.eclipse.bpmn2.OutputSet;
import org.eclipse.bpmn2.Property;
import org.kie.workbench.common.stunner.bpmn.backend.converters.properties.Attribute;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.VariableDeclaration;

import static org.kie.workbench.common.stunner.bpmn.backend.fromstunner.Factories.bpmn2;

public class OutputAssignmentWriter {

    private final String parentId;
    private final DataOutputAssociation association;
    private final VariableDeclaration decl;
    private final OutputSet outputSet;
    private final DataOutput source;
    private ItemDefinition typeDef;

    public OutputAssignmentWriter(
            String parentId,
            VariableDeclaration decl,
            VariableScope.Variable variable) {
        this.parentId = parentId;
        this.decl = decl;

        // first we declare the type of this assignment
        this.typeDef = typedefOutput(decl);

        // then we declare the input that will provide
        // the value that we assign to `source`
        // e.g. myTarget
        this.source = writeOutputTo(decl.getIdentifier(), typeDef);

        // then we create the actual association between the two
        // e.g. mySource := myTarget (or, to put it differently, myTarget -> mySource)
        this.association = associationOf(variable.getTypedIdentifier(), source);

        this.outputSet = bpmn2.createOutputSet();
        this.outputSet.getDataOutputRefs().add(source);
    }

    private DataOutputAssociation associationOf(Property source, DataOutput dataOutput) {
        DataOutputAssociation dataOutputAssociation =
                bpmn2.createDataOutputAssociation();

        dataOutputAssociation
                .getSourceRef()
                .add(dataOutput);

        dataOutputAssociation
                .setTargetRef(source);
        return dataOutputAssociation;
    }

    private DataOutput writeOutputTo(String sourceName, ItemDefinition typeDef) {
        DataOutput dataOutput = bpmn2.createDataOutput();
        // the id is an encoding of the node id + the name of the output
        dataOutput.setId(dataOutputId());
        dataOutput.setName(sourceName);
        dataOutput.setItemSubjectRef(typeDef);
        Attribute.dtype.of(dataOutput).set(typeDef.getStructureRef());
        return dataOutput;
    }

    private Property varDecl(String varName, ItemDefinition typeDef) {
        Property source = bpmn2.createProperty();
        source.setId(propertyId(varName));
        source.setName(varName);
        source.setItemSubjectRef(typeDef);
        return source;
    }

    private ItemDefinition typedefOutput(VariableDeclaration decl) {
        ItemDefinition typeDef = bpmn2.createItemDefinition();
        typeDef.setId(itemId());
        typeDef.setStructureRef(decl.getType());
        return typeDef;
    }

    private String dataOutputId() {
        return parentId + "_" + decl.getIdentifier() + "OutputX";
    }

    private String itemId() {
        return "_" + dataOutputId() + "Item";
    }

    private String propertyId(String id) {
        return "prop_" + id + dataOutputId();
    }

    public DataOutput getDataOutput() {
        return source;
    }

    public OutputSet getOutputSet() {
        return outputSet;
    }

    public DataOutputAssociation getAssociation() {
        return association;
    }

    public ItemDefinition getItemDefinition() {
        return typeDef;
    }
}
