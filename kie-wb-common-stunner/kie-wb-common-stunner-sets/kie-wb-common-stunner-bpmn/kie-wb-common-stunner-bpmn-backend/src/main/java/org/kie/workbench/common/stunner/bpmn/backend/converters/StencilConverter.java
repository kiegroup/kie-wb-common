package org.kie.workbench.common.stunner.bpmn.backend.converters;

import com.fasterxml.jackson.core.JsonGenerator;

import java.io.IOException;

public class StencilConverter {
    public static void convert(String stencilId, JsonGenerator generator) throws IOException {
        generator.writeObjectFieldStart("stencil");
        generator.writeObjectField("id",
                stencilId);
        generator.writeEndObject();
    }

}
