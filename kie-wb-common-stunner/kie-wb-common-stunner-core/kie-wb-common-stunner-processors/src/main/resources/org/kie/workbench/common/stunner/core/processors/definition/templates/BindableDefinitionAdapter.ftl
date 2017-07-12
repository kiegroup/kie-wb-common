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

import ${parentAdapterClassName};
import ${adapterFactoryClassName};

import javax.annotation.Generated;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterFactory;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableDefinitionAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableDefinitionAdapterProxy;
import org.kie.workbench.common.stunner.core.definition.DynamicDefinition;
import org.kie.workbench.common.stunner.core.definition.DynamicDefinitions;

@Generated("${generatedByClassName}")
@ApplicationScoped
public class ${className} extends ${parentAdapterClassName}<Object> implements DynamicDefinitions.DynamicDefinitionListener {

    private static final Map<Class, Class> baseTypes = new HashMap<Class, Class>(${baseTypesSize}) {{
        <#list baseTypes as baseType>
            put( ${baseType.className}.class, ${baseType.methodName}.class );
        </#list>
    }};

    private static final Map<Class, String> categoryFieldNames = new HashMap<Class, String>(${categoryFieldNamesSize}) {{
        <#list categoryFieldNames as categoryFieldName>
            put( ${categoryFieldName.className}.class, "${categoryFieldName.methodName}" );
        </#list>
    }};

    private static final Map<Class, String> titleFieldNames = new HashMap<Class, String>(${titleFieldNamesSize}) {{
        <#list titleFieldNames as titleFieldName>
            put( ${titleFieldName.className}.class, "${titleFieldName.methodName}" );
        </#list>
    }};

    private static final Map<Class, String> descriptionFieldNames = new HashMap<Class, String>(${descriptionFieldNamesSize}) {{
        <#list descriptionFieldNames as descriptionFieldName>
              put( ${descriptionFieldName.className}.class, "${descriptionFieldName.methodName}" );
        </#list>
    }};

    private static final Map<Class, String> labelsFieldNames = new HashMap<Class, String>(${labelsFieldNamesSize}) {{
        <#list labelsFieldNames as labelsFieldName>
              put( ${labelsFieldName.className}.class, "${labelsFieldName.methodName}" );
        </#list>
    }};

    private static final Map<Class, Class> graphFactoryFieldNames = new HashMap<Class, Class>(${graphFactoryFieldNamesSize}) {{
        <#list graphFactoryFieldNames as graphFactoryFieldName>
              put( ${graphFactoryFieldName.className}.class, ${graphFactoryFieldName.methodName}.class );
        </#list>
    }};

    private static final Map<Class, Set<String>> propertySetsFieldNames = new HashMap<Class, Set<String>>(${propertySetsFieldNamesSize}) {{
        <#list propertySetsFieldNames as propertySetsFieldName>
           put( ${propertySetsFieldName.className}.class, new HashSet<String>() {{
                <#list propertySetsFieldName.elements as subElem>
                    add ( "${subElem}" );
                </#list>
            }} );
        </#list>
    }};

    private static final Map<Class, Set<String>> propertiesFieldNames = new HashMap<Class, Set<String>>(${propertiesFieldNamesSize}) {{
        <#list propertiesFieldNames as propertiesFieldName>
            put( ${propertiesFieldName.className}.class, new HashSet<String>() {{
            <#list propertiesFieldName.elements as subElem>
                add ( "${subElem}" );
            </#list>
            }} );
        </#list>
    }};

    private static final Map<${metaTypeClass}, Class> metaPropertyTypes = new HashMap<${metaTypeClass}, Class>(${metaTypesSize}) {{
        <#list metaTypes as metaType>
            put( ${metaType.className}, ${metaType.methodName} );
        </#list>
    }};

    private static final Set<Class<?>> addonGroups = new HashSet<Class<?>>(${addonGroupsSize}) {{
        <#list addonGroups as addonGroup>
            add( ${addonGroup} );
        </#list>
    }};

    @Inject
    private DynamicDefinitions dynamicDefinitions;

    protected ${className}() {
    }

    @Inject
    public ${className}(${adapterFactoryClassName} adapterFactory) {
        super(adapterFactory);
    }

    @PostConstruct
    private void addDynamicDefinitions()
    {
        dynamicDefinitions.registerDynamicDefinitionListener(this);
        baseTypes.putAll(dynamicDefinitions.getBaseTypes(addonGroups));
        categoryFieldNames.putAll(dynamicDefinitions.getCategoryFieldNames(addonGroups));
        titleFieldNames.putAll(dynamicDefinitions.getTitleFieldNames(addonGroups));
        descriptionFieldNames.putAll(dynamicDefinitions.getDescriptionFieldNames(addonGroups));
        labelsFieldNames.putAll(dynamicDefinitions.getLabelsFieldNames(addonGroups));
        graphFactoryFieldNames.putAll(dynamicDefinitions.getGraphFactoryFieldNames(addonGroups));
        propertySetsFieldNames.putAll(dynamicDefinitions.getPropertySetsFieldNames(addonGroups));
        propertiesFieldNames.putAll(dynamicDefinitions.getPropertiesFieldNames(addonGroups));
        metaPropertyTypes.putAll(dynamicDefinitions.getMetaPropertyTypes(addonGroups));
    }

    @Override
    public void onDynamicDefinitionAdded(DynamicDefinition def) {
        if (DynamicDefinitions.inAddonGroups(def, addonGroups)) {
            Class<?> clazz = def.getType();
            baseTypes.put(clazz, def.getBaseType());
            categoryFieldNames.put(clazz, "category");
            titleFieldNames.put(clazz, "title");
            descriptionFieldNames.put(clazz, "description");
            labelsFieldNames.put(clazz, "labels");
            graphFactoryFieldNames.put(clazz, def.getFactory());
            propertySetsFieldNames.put(clazz, def.getSetProperties());
            propertiesFieldNames.put(clazz, def.getProperties());
        }
    }

    @Override
    protected void setBindings(final BindableDefinitionAdapter<Object> adapter) {
        adapter.setBindings( metaPropertyTypes,
                baseTypes,
                propertySetsFieldNames,
                propertiesFieldNames,
                graphFactoryFieldNames,
                labelsFieldNames,
                titleFieldNames,
                categoryFieldNames,
                descriptionFieldNames);
    }

}
