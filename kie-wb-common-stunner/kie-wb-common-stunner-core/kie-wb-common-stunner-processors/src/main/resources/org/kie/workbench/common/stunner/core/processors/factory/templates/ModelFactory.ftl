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

import javax.annotation.Generated;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.LinkedHashSet;
import java.util.Set;

import org.kie.workbench.common.stunner.core.definition.DynamicDefinition;
import org.kie.workbench.common.stunner.core.definition.DynamicDefinitions;

@Generated("${generatedByClassName}")
@ApplicationScoped
public class ${className} extends ${parentClassName}<Object> implements DynamicDefinitions.DynamicDefinitionListener {

    private static final Set<Class<?>> SUPPORTED_DEF_CLASSES = new LinkedHashSet<Class<?>>() {{

        <#list builders as builder>
            add( ${builder.className}.class );
        </#list>
    }};

    private static final Set<Class<?>> addonGroups = new LinkedHashSet<Class<?>>(${addonGroupsSize}) {{
                <#list addonGroups as addonGroup>
                    add( ${addonGroup} );
                </#list>
    }};

    @Inject
    private DynamicDefinitions dynamicDefinitions;

    public ${className}() {
    }

    @PostConstruct
    private void addDynamicDefinitions() {
        dynamicDefinitions.registerDynamicDefinitionListener(this);
        SUPPORTED_DEF_CLASSES.addAll(dynamicDefinitions.getSupportedClasses(addonGroups));
    }

    @Override
    public void onDynamicDefinitionAdded(DynamicDefinition def) {
        if (DynamicDefinitions.inAddonGroups(def, addonGroups) && !SUPPORTED_DEF_CLASSES.contains(def.getType())) {
            SUPPORTED_DEF_CLASSES.add(def.getType());
        }
    }

    @Override
    public Set<Class<?>> getAcceptedClasses() {
        return SUPPORTED_DEF_CLASSES;
    }

    @Override
    public Object build( final Class<?> clazz ) {

        <#list builders as builder>

            if ( ${builder.className}.class.equals( clazz ) ) {

                return new ${builder.methodName}().build();

            }

        </#list>

        if ( dynamicDefinitions.getSupportedClasses(addonGroups).contains(clazz) ) {
            return dynamicDefinitions.getInstanceOf(clazz);
        }

        throw new RuntimeException( "This factory [" + this.getClass().getName() + "] " +
            "should provide a definition for [" + clazz + "]" );
    }

}
