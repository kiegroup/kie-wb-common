package org.kie.workbench.common.stunner.bpmn.backend.service.diagram;

import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.oryx.Bpmn2OryxIdMappings;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.oryx.Bpmn2OryxManager;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.oryx.property.*;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;

import java.util.LinkedList;
import java.util.List;

public class Oryx {
    public static Bpmn2OryxManager createOryxManager(
            DefinitionManager definitionManager, DefinitionUtils definitionUtils) {
        // Bpmn 2 oryx stuff.
        Bpmn2OryxIdMappings oryxIdMappings = new Bpmn2OryxIdMappings(definitionManager);
        StringTypeSerializer stringTypeSerializer = new StringTypeSerializer();
        BooleanTypeSerializer booleanTypeSerializer = new BooleanTypeSerializer();
        ColorTypeSerializer colorTypeSerializer = new ColorTypeSerializer();
        DoubleTypeSerializer doubleTypeSerializer = new DoubleTypeSerializer();
        IntegerTypeSerializer integerTypeSerializer = new IntegerTypeSerializer();
        EnumTypeSerializer enumTypeSerializer = new EnumTypeSerializer(definitionUtils);
        AssignmentsTypeSerializer assignmentsTypeSerializer = new AssignmentsTypeSerializer();
        VariablesTypeSerializer variablesTypeSerializer = new VariablesTypeSerializer();
        TimerSettingsTypeSerializer timerSettingsTypeSerializer = new TimerSettingsTypeSerializer();
        List<Bpmn2OryxPropertySerializer<?>> propertySerializers = new LinkedList<>();
        propertySerializers.add(stringTypeSerializer);
        propertySerializers.add(booleanTypeSerializer);
        propertySerializers.add(colorTypeSerializer);
        propertySerializers.add(doubleTypeSerializer);
        propertySerializers.add(integerTypeSerializer);
        propertySerializers.add(enumTypeSerializer);
        propertySerializers.add(assignmentsTypeSerializer);
        propertySerializers.add(variablesTypeSerializer);
        propertySerializers.add(timerSettingsTypeSerializer);

        Bpmn2OryxPropertyManager oryxPropertyManager = new Bpmn2OryxPropertyManager(propertySerializers);
        Bpmn2OryxManager oryxManager = new Bpmn2OryxManager(oryxIdMappings,
                oryxPropertyManager);
        oryxManager.init();
        return oryxManager;
    }
}
