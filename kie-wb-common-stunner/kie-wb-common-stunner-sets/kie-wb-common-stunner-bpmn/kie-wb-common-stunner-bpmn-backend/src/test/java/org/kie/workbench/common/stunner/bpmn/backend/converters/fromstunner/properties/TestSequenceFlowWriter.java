package org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties;

import org.eclipse.bpmn2.ScriptTask;
import org.kie.workbench.common.stunner.core.graph.content.view.BoundsImpl;

import static org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.Factories.bpmn2;

class TestSequenceFlowWriter {

    final PropertyWriterFactory propertyWriter = new PropertyWriterFactory();

    SequenceFlowPropertyWriter sequenceFlowOf(String id) {
        org.eclipse.bpmn2.SequenceFlow sequenceFlow = bpmn2.createSequenceFlow();
        sequenceFlow.setId(id);
        return propertyWriter.of(sequenceFlow);
    }

    PropertyWriter nodeOf(String id, float x, float y, float width, float height) {
        ScriptTask el = bpmn2.createScriptTask();
        el.setId(id);
        BoundsImpl sb = BoundsImpl.build(x, y, x + width, y + height);
        PropertyWriter p = propertyWriter.of(el);
        p.setBounds(sb);
        return p;
    }
}
