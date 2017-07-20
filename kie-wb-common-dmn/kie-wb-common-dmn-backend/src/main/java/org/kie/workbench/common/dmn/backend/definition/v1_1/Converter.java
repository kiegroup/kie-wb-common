package org.kie.workbench.common.dmn.backend.definition.v1_1;

import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

interface Converter<D extends org.kie.dmn.model.v1_1.DMNModelInstrumentedBase, W extends org.kie.workbench.common.dmn.api.definition.v1_1.DMNModelInstrumentedBase> {

    Node<View<W>, ?> nodeFromDMN(D source);
    
    D dmnFromNode(Node<View<W>, ?> source);

}
