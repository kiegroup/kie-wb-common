package org.kie.workbench.common.stunner.bpmn.backend.converters.properties;

import org.eclipse.bpmn2.Assignment;
import org.eclipse.bpmn2.DataInput;
import org.eclipse.bpmn2.DataInputAssociation;
import org.eclipse.bpmn2.FormalExpression;
import org.eclipse.bpmn2.InputOutputSpecification;
import org.eclipse.bpmn2.ItemDefinition;
import org.eclipse.bpmn2.Property;
import org.eclipse.bpmn2.Task;

import static org.kie.workbench.common.stunner.bpmn.backend.fromstunner.Factories.bpmn2;

public class CustomInput<T> {

    public static final CustomInputDefinition<String> taskName = new StringInput("TaskName","Task");
    public static final CustomInputDefinition<String> priority = new StringInput("Priority", "");
    public static final CustomInputDefinition<String> subject = new StringInput("Comment", "");
    public static final CustomInputDefinition<String> description = new StringInput("Description", "");
    public static final CustomInputDefinition<String> createdBy = new StringInput("CreatedBy", "");
    public static final CustomInputDefinition<String> groupId = new StringInput("GroupId", "");
    public static final CustomInputDefinition<Boolean> skippable = new BooleanInput("Skippable", false);

    private final CustomInputDefinition<T> inputDefinition;
    private final Task element;
    private final ItemDefinition typeDef;

    public CustomInput(CustomInputDefinition<T> inputDefinition, Task element) {
        this.inputDefinition = inputDefinition;
        this.element = element;
        this.typeDef = typedefInput(inputDefinition.name(), inputDefinition.type());
    }

    public ItemDefinition typeDef() {
        return typeDef;
    }

    public T get() {
        return inputDefinition.getValue(element);
    }

    public void set(T value) {
        setStringValue(String.valueOf(value));
    }

    void setStringValue(String value) {
        if (value == null || value.isEmpty()) {
            return;
        }
        DataInputAssociation input = input(value);
        getIoSpecification(element).getDataInputs().add((DataInput) input.getTargetRef());
        element.getDataInputAssociations().add(input);
    }

    private InputOutputSpecification getIoSpecification(Task element) {
        InputOutputSpecification ioSpecification = element.getIoSpecification();
        if (ioSpecification == null) {
            ioSpecification = bpmn2.createInputOutputSpecification();
            element.setIoSpecification(ioSpecification);
        }
        return ioSpecification;
    }

    private DataInputAssociation input(Object value) {
        // first we declare the type of this assignment

        Property decl = varDecl(inputDefinition.name(), typeDef);

//        // then we declare the input that will provide
//        // the value that we assign to `source`
//        // e.g. myInput
        DataInput target = readInputFrom(inputDefinition.name(), typeDef);

        Assignment assignment = assignment(value.toString(), target.getId());

        // then we create the actual association between the two
        // e.g. foo := myInput (or, to put it differently, myInput -> foo)
        DataInputAssociation dataInputAssociation =
                associate(assignment, target);

        dataInputAssociation.setId("AAAAA" + makeDataInputId(inputDefinition.name()));
        dataInputAssociation.setTargetRef(target);

        return dataInputAssociation;
    }

    private DataInput readInputFrom(String targetName, ItemDefinition typeDef) {
        DataInput dataInput = bpmn2.createDataInput();
        dataInput.setName(targetName);
        // the id is an encoding of the node id + the name of the input
        dataInput.setId(makeDataInputId(targetName));
        dataInput.setItemSubjectRef(typeDef);
        Attribute.dtype.of(dataInput).set(typeDef.getStructureRef());
        return dataInput;
    }

    private String makeDataInputId(String targetName) {
        return element.getId() + "_" + targetName + "InputX";
    }

    private Assignment assignment(String from, String to) {
        Assignment assignment = bpmn2.createAssignment();
        FormalExpression fromExpr = bpmn2.createFormalExpression();
        fromExpr.setBody(from);
        assignment.setFrom(fromExpr);
        FormalExpression toExpr = bpmn2.createFormalExpression();
        toExpr.setBody(to);
        assignment.setTo(toExpr);
        return assignment;
    }

    private DataInputAssociation associate(Assignment assignment, DataInput dataInput) {
        DataInputAssociation dataInputAssociation =
                bpmn2.createDataInputAssociation();

        dataInputAssociation.getAssignment()
                .add(assignment);

        dataInputAssociation
                .setTargetRef(dataInput);
        return dataInputAssociation;
    }

    private Property varDecl(String varName, ItemDefinition typeDef) {
        Property source = bpmn2.createProperty();
        source.setId(varName);
        source.setItemSubjectRef(typeDef);
        return source;
    }

    private ItemDefinition typedefInput(String name, String type) {
        ItemDefinition typeDef = bpmn2.createItemDefinition();
        typeDef.setId("_" + makeDataInputId(name) + "Item");
        typeDef.setStructureRef(type);
        return typeDef;
    }
}
