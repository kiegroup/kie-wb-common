/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

public class SLASettingsValueValidatorTest
        extends GWTTestCase {

    private SLASettingsValueValidator validator;

    private ConstraintValidatorContext context;

    private String value;

    private List<String> errorMessages = new ArrayList<>();

    private List<TestElement> testElements = new ArrayList<>();

    private static final String[] VALID_TIME_DURATIONS = {
            "P6D",
            "P6DT1H",
            "P6DT1H8M",
            "P6DT1H8M15S",
            "PT1H",
            "PT1H8M",
            "PT1H8M5S",
            "PT8M",
            "PT3S"
    };

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

    private static final String[] VALID_EXPRESSIONS = {
            "#{something}"
    };

    private static final String[] INVALID_EXPRESSIONS = {
            "#",
            "{",
            "#{",
            "#{}",
            "#}",
            "}",
            "etc"
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
        loadValidTestElements(VALID_TIME_DURATIONS);
        loadValidTestElements(VALID_EXPRESSIONS);
        loadInvalidTestElements(SLASettingsValueValidator.TimeDurationInvalid,
                                INVALID_TIME_DURATIONS);
        loadInvalidTestElements(SLASettingsValueValidator.TimeDurationInvalid,
                                INVALID_EXPRESSIONS);
        testElements.forEach(testElement -> {
            value = testElement.getValue();
            testElement.setResult(validator.isValid(new SLADueDate(value),
                                                    context));
        });
        verifyTestResults();
    }

    private void loadValidTestElements(String... values) {
        Arrays.stream(values).forEach(value -> testElements.add(new TestElement(value,
                                                                                true)));
    }

    private void loadInvalidTestElements(String errorMessage,
                                         String... values) {
        Arrays.stream(values).forEach(value -> testElements.add(new TestElement(value,
                                                                                false,
                                                                                errorMessage)));
    }

    private void verifyTestResults() {
        int error = 0;
        for (int i = 0; i < testElements.size(); i++) {
            TestElement testElement = testElements.get(i);
            assertEquals("Invalid validation for item: " + testElement.toString(),
                         testElement.getExpectedResult(),
                         testElement.getResult());

            if (!testElement.getExpectedResult()) {
                assertEquals("Invalid validation: " + testElement.toString(),
                             testElement.getExpectedError(),
                             errorMessages.get(error));
                error++;
            }
        }
    }

    private void clear() {
        testElements.clear();
        errorMessages.clear();
    }

    private class TestElement {

        private String value = null;
        private boolean expectedResult;
        private String expectedError = null;
        private boolean result;

        public TestElement(String value,
                           boolean expectedResult) {
            this.value = value;
            this.expectedResult = expectedResult;
        }

        public TestElement(String value,
                           boolean expectedResult,
                           String expectedError) {
            this.value = value;
            this.expectedResult = expectedResult;
            this.expectedError = expectedError;
        }

        public String getValue() {
            return value;
        }

        public boolean getExpectedResult() {
            return expectedResult;
        }

        public String getExpectedError() {
            return expectedError;
        }

        public boolean getResult() {
            return result;
        }

        public void setResult(boolean result) {
            this.result = result;
        }

        @Override
        public String toString() {
            return "TestElement{" +
                    "value='" + value + '\'' +
                    ", expectedResult=" + expectedResult +
                    ", expectedError='" + expectedError + '\'' +
                    ", result=" + result +
                    '}';
        }
    }
}
