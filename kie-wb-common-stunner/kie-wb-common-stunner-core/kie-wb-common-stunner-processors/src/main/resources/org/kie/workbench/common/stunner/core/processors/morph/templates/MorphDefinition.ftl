/*
* Copyright 2016 Red Hat, Inc. and/or its affiliates.
*  
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*  
*    http://www.apache.org/licenses/LICENSE-2.0
*  
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package ${packageName};

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import javax.annotation.Generated;
import javax.inject.Inject;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.stunner.core.definition.DynamicDefinition;
import org.kie.workbench.common.stunner.core.definition.DynamicDefinitions;

@Generated("${generatedByClassName}")
@Portable
public class ${className} extends ${parentClassName} implements DynamicDefinitions.DynamicDefinitionListener {
    private static final  Map<Class<?>, Collection<Class<?>>> DOMAIN_MORPHS =
        new HashMap<Class<?>, Collection<Class<?>>>( 1 ) {{
            put( ${morphBaseClassName}.class,
                new ArrayList<Class<?>>( ${targetClassNamesSize} ) {{
                    <#list targetClassNames as targetClassName>
                        add( ${targetClassName}.class );
                    </#list>
                    DynamicDefinitions dynamicDefinitions = new DynamicDefinitions();
                    ${className} instance = new ${className}();
                    dynamicDefinitions.registerDynamicDefinitionListener(instance);
                    addAll(dynamicDefinitions.getDomainMorphs(${morphBaseClassName}.class, addonGroups));
                }} );
        }};

    private static final Set<Class<?>> addonGroups = new HashSet<Class<?>>(${addonGroupsSize}) {{
                        <#list addonGroups as addonGroup>
                            add( ${addonGroup} );
                        </#list>
        }};

    public ${className}() {
    }

    @Override
    protected Class<?> getDefaultType() {
        return ${defaultTypeClassName}.class;
    }

    @Override
    public void onDynamicDefinitionAdded(DynamicDefinition def) {
        if (DynamicDefinitions.inAddonGroups(def, addonGroups)) {
            if (${morphBaseClassName}.class.equals(def.getBaseType()) &&
                !DOMAIN_MORPHS.get(${morphBaseClassName}.class).contains(def.getType())) {
                DOMAIN_MORPHS.get(${morphBaseClassName}.class).add(def.getType());
            }
        }
    }


    @Override
    protected Map<Class<?>, Collection<Class<?>>> getDomainMorphs() {
        return DOMAIN_MORPHS;
    }

}
