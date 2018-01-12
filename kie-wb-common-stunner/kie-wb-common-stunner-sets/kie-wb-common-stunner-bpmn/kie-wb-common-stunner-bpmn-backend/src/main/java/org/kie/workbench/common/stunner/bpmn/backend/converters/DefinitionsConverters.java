package org.kie.workbench.common.stunner.bpmn.backend.converters;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.eclipse.bpmn2.RootElement;

public class DefinitionsConverters {
    public static Stream<RootElement> asStream(org.eclipse.bpmn2.Definitions definitions) {
        return StreamSupport.stream(definitions.getRootElements().spliterator(), false);
    }

}
