package org.kie.workbench.common.stunner.bpmn.definition.property.task;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.definition.ParallelGateway;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Name;

import static org.junit.Assert.assertTrue;

public class ParallelGatewayTest {

    private Validator validator;

    private static final String NAME_VALID = "Gateway";

    @Before
    public void init() {
        ValidatorFactory vf = Validation.buildDefaultValidatorFactory();
        this.validator = vf.getValidator();
    }

    @Test
    public void testParallelDatabasedGatewayNameValid() {
        ParallelGateway parallelGateway = new ParallelGateway.ParallelGatewayBuilder().build();
        parallelGateway.getGeneral().setName(new Name(NAME_VALID));
        Set<ConstraintViolation<ParallelGateway>> violations = this.validator.validate(parallelGateway);
        assertTrue(violations.isEmpty());
    }

    @Test
    public void testParallelDatabasedGatewayNameEmpty() {
        ParallelGateway parallelGateway = new ParallelGateway.ParallelGatewayBuilder().build();
        parallelGateway.getGeneral().setName(new Name(""));
        Set<ConstraintViolation<ParallelGateway>> violations = this.validator.validate(parallelGateway);
        assertTrue(violations.isEmpty());
    }
}