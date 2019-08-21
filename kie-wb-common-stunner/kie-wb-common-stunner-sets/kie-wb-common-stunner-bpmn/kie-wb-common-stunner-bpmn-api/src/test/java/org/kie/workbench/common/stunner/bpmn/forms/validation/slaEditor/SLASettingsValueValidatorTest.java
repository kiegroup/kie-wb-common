/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.bpmn.forms.validation.slaEditor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.validation.ConstraintValidatorContext;

import com.google.gwt.junit.client.GWTTestCase;
import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.SLADueDate;
import org.kie.workbench.common.stunner.bpmn.forms.validation.timerEditor.TimerSettingsValueValidatorTest;

public class SLASettingsValueValidatorTest
        extends GWTTestCase {

    private SLASettingsValueValidator validator;

    private ConstraintValidatorContext context;

    private String value;

    private List<String> errorMessages = new ArrayList<>();

    private List<TimerSettingsValueValidatorTest.TestElement> testElements = new ArrayList<>();

    private static final String[] INVALID_TIME_DURATIONS = {
            "P4Y",
            "P4Y2M",
            "P4Y2M6D",
            "P4Y2M6DT1H",
            "P4Y2M6DT1H8M",
            "P4Y2M6DT1H8M15S",
            "PPP",
            "23EE",
            "etc",
    };

    @Override
    protected void gwtSetUp() throws Exception {
        super.gwtSetUp();
        validator = new SLASettingsValueValidator();
        value = "";
        context = new ConstraintValidatorContext() {
            @Override
            public void disableDefaultConstraintViolation() {
            }

            @Override
            public String getDefaultConstraintMessageTemplate() {
                return null;
            }

            @Override
            public ConstraintViolationBuilder buildConstraintViolationWithTemplate(String message) {
                errorMessages.add(message);
                return new ConstraintViolationBuilder() {
                    @Override
                    public NodeBuilderDefinedContext addNode(String name) {
                        return null;
                    }

                    @Override
                    public ConstraintValidatorContext addConstraintViolation() {
                        return context;
                    }
                };
            }
        };
    }

    @Override
    public String getModuleName() {
        return "org.kie.workbench.common.stunner.bpmn.forms.validation.SLASettingsValueValidatorTest";
    }

    @Test
    public void testValidateTimeDuration() {
        clear();
        loadValidTestElements(TimerSettingsValueValidatorTest.VALID_TIME_DURATIONS);
        loadValidTestElements(TimerSettingsValueValidatorTest.VALID_EXPRESSIONS);
        loadInvalidTestElements(SLASettingsValueValidator.TimeDurationInvalid,
                                INVALID_TIME_DURATIONS);
        loadInvalidTestElements(SLASettingsValueValidator.TimeDurationInvalid,
                                TimerSettingsValueValidatorTest.INVALID_EXPRESSIONS);
        testElements.forEach(testElement -> {
            value = testElement.getValue();
            testElement.setResult(validator.isValid(new SLADueDate(value),
                                                    context));
        });
        TimerSettingsValueValidatorTest.verifyTestResults(testElements, errorMessages);
    }

    private void loadValidTestElements(String... values) {
        Arrays.stream(values).forEach(value -> testElements.add(new TimerSettingsValueValidatorTest.TestElement(value,
                                                                                true)));
    }

    private void loadInvalidTestElements(String errorMessage,
                                         String... values) {
        Arrays.stream(values).forEach(value -> testElements.add(new TimerSettingsValueValidatorTest.TestElement(value,
                                                                                false,
                                                                                errorMessage)));
    }

    private void clear() {
        testElements.clear();
        errorMessages.clear();
    }
}
