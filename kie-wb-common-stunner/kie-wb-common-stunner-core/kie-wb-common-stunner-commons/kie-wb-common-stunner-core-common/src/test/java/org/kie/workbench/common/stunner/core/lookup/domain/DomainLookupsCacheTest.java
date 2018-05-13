/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.lookup.domain;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.definition.adapter.AdapterManager;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionSetAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionSetRuleAdapter;
import org.kie.workbench.common.stunner.core.registry.definition.TypeDefinitionSetRegistry;
import org.kie.workbench.common.stunner.core.registry.impl.DefinitionsCacheRegistry;
import org.kie.workbench.common.stunner.core.rule.RuleSet;
import org.kie.workbench.common.stunner.core.rule.RuleSetImpl;
import org.kie.workbench.common.stunner.core.rule.impl.CanConnect;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DomainLookupsCacheTest {

    private static final String DEF_SET_ID = "ds1";
    private static final String DEF_ID1 = "defId1";
    private static final String DEF_ID2 = "defId2";
    private static final String ROLE1 = "role1";
    private static final String ROLE2 = "role2";
    private static final CanConnect canConnect1To2 = new CanConnect("1to2",
                                                                    DEF_ID2,
                                                                    Collections.singletonList(new CanConnect.PermittedConnection(ROLE1,
                                                                                                                                 ROLE2)));
    private static final RuleSet RULE_SET = new RuleSetImpl("ruleSet1",
                                                            Arrays.asList(canConnect1To2));

    @Mock
    private DefinitionManager definitionManager;

    @Mock
    private TypeDefinitionSetRegistry definitionSetRegistry;

    @Mock
    private AdapterManager adapterManager;

    @Mock
    private DefinitionSetRuleAdapter ruleAdapter;

    @Mock
    private DefinitionSetAdapter definitionSetAdapter;

    @Mock
    private DefinitionsCacheRegistry definitionsRegistry;

    @Mock
    private Object definitionSet;

    @Mock
    private Object definition1;

    @Mock
    private Object definition2;

    private DomainLookupsCache tested;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() throws Exception {
        when(definitionManager.adapters()).thenReturn(adapterManager);
        when(adapterManager.forDefinitionSet()).thenReturn(definitionSetAdapter);
        when(adapterManager.forRules()).thenReturn(ruleAdapter);
        when(definitionManager.definitionSets()).thenReturn(definitionSetRegistry);
        when(definitionSetRegistry.getDefinitionSetById(eq(DEF_SET_ID))).thenReturn(definitionSet);
        when(ruleAdapter.getRuleSet(eq(definitionSet))).thenReturn(RULE_SET);
        when(definitionSetAdapter.getDefinitions(eq(definitionSet))).thenReturn(new HashSet<String>(2) {{
            add(DEF_ID1);
            add(DEF_ID2);
        }});
        when(definitionsRegistry.getLabels(eq(DEF_ID1))).thenReturn(Collections.singleton(ROLE1));
        when(definitionsRegistry.getLabels(eq(DEF_ID2))).thenReturn(Collections.singleton(ROLE2));
        tested = new DomainLookupsCache(definitionManager,
                                        definitionsRegistry,
                                        DEF_SET_ID);
    }

    @Test
    public void testGetDefinitionSetId() {
        assertEquals(DEF_SET_ID, tested.getDefinitionSetId());
    }

    @Test
    public void testCacheTheConnectionRules() {
        List<CanConnect> rules = tested.getConnectionRules();
        assertTrue(rules.size() == 1);
        assertEquals(canConnect1To2, rules.get(0));
    }

    @Test
    public void testGetDefinitionsByLabel() {
        Set<String> defs1 = tested.getDefinitions(ROLE1);
        assertTrue(defs1.size() == 1);
        assertEquals(DEF_ID1, defs1.iterator().next());
        Set<String> defs2 = tested.getDefinitions(ROLE2);
        assertTrue(defs2.size() == 1);
        assertEquals(DEF_ID2, defs2.iterator().next());
    }
}
