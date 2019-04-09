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

package org.kie.workbench.common.dmn.client.decision.included.components;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.v1_1.DRGElement;
import org.kie.workbench.common.dmn.api.definition.v1_1.Decision;
import org.kie.workbench.common.dmn.api.definition.v1_1.InputData;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DecisionComponentFilterTest {

    private DecisionComponentFilter filter;

    @Before
    public void setup() {
        filter = new DecisionComponentFilter();
    }

    @Test
    public void testGetDrgElementWhenDrgElementIsNotPresent() {
        assertFalse(filter.getDrgElement().isPresent());
    }

    @Test
    public void testGetDrgElement() {
        final String decision = "Decision";

        filter.setDrgElement(decision);

        assertTrue(filter.getDrgElement().isPresent());
        assertEquals(decision, filter.getDrgElement().get());
    }

    @Test
    public void testGetTermWhenDrgElementIsNotPresent() {
        assertFalse(filter.getTerm().isPresent());
    }

    @Test
    public void testGetTerm() {
        final String term = "term";

        filter.setTerm(term);

        assertTrue(filter.getTerm().isPresent());
        assertEquals(term, filter.getTerm().get());
    }

    @Test
    public void testQueryWithoutFilters() {

        final DecisionComponentsItem item1 = item("Can Drive?", Decision.class);
        final DecisionComponentsItem item2 = item("Is Allowed?", Decision.class);
        final DecisionComponentsItem item3 = item("Age", InputData.class);
        final DecisionComponentsItem item4 = item("Name", InputData.class);
        final Stream<DecisionComponentsItem> stream = Stream.of(item1, item2, item3, item4);

        final Stream<DecisionComponentsItem> query = filter.query(stream);
        final List<DecisionComponentsItem> actualResult = query.collect(Collectors.toList());
        final List<DecisionComponentsItem> expectedResult = asList(item1, item2, item3, item4);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testQueryFilteredByTerm() {

        final DecisionComponentsItem item1 = item("Can Drive?", Decision.class);
        final DecisionComponentsItem item2 = item("Is Allowed?", Decision.class);
        final DecisionComponentsItem item3 = item("Age", InputData.class);
        final DecisionComponentsItem item4 = item("Name", InputData.class);
        final Stream<DecisionComponentsItem> stream = Stream.of(item1, item2, item3, item4);

        filter.setTerm("name");

        final Stream<DecisionComponentsItem> query = filter.query(stream);
        final List<DecisionComponentsItem> actualResult = query.collect(Collectors.toList());
        final List<DecisionComponentsItem> expectedResult = singletonList(item4);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testQueryFilteredByDrgElement() {

        final DecisionComponentsItem item1 = item("Can Drive?", Decision.class);
        final DecisionComponentsItem item2 = item("Is Allowed?", Decision.class);
        final DecisionComponentsItem item3 = item("Age", InputData.class);
        final DecisionComponentsItem item4 = item("Name", InputData.class);
        final Stream<DecisionComponentsItem> stream = Stream.of(item1, item2, item3, item4);

        filter.setDrgElement("Decision");

        final Stream<DecisionComponentsItem> query = filter.query(stream);
        final List<DecisionComponentsItem> actualResult = query.collect(Collectors.toList());
        final List<DecisionComponentsItem> expectedResult = asList(item1, item2);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testQueryFilteredByDrgElementAndTerm() {

        final DecisionComponentsItem item1 = item("Can Drive?", Decision.class);
        final DecisionComponentsItem item2 = item("Is Allowed?", Decision.class);
        final DecisionComponentsItem item3 = item("Age", InputData.class);
        final DecisionComponentsItem item4 = item("Name", InputData.class);
        final Stream<DecisionComponentsItem> stream = Stream.of(item1, item2, item3, item4);

        filter.setTerm("is");
        filter.setDrgElement("Decision");

        final Stream<DecisionComponentsItem> query = filter.query(stream);
        final List<DecisionComponentsItem> actualResult = query.collect(Collectors.toList());
        final List<DecisionComponentsItem> expectedResult = singletonList(item2);

        assertEquals(expectedResult, actualResult);
    }

    private DecisionComponentsItem item(final String drgElementName,
                                        final Class<? extends DRGElement> drgElementClass) {

        final DecisionComponentsItem item = mock(DecisionComponentsItem.class);
        final DecisionComponent decisionComponent = new DecisionComponent(null, null, drgElementName, drgElementClass);

        when(item.getDecisionComponent()).thenReturn(decisionComponent);

        return item;
    }
}
