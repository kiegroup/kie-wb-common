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

package org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties;

import java.util.Optional;

import com.google.gwt.http.client.URL;
import org.eclipse.bpmn2.Assignment;
import org.eclipse.bpmn2.DataOutput;
import org.eclipse.bpmn2.DataOutputAssociation;
import org.eclipse.bpmn2.FormalExpression;
import org.eclipse.bpmn2.Property;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.customproperties.AssociationDeclaration;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.util.FormalExpressionBodyHandler;

public class OutputAssignmentReader {

    private final AssociationDeclaration associationDeclaration;

    public static OutputAssignmentReader fromAssociation(DataOutputAssociation out) {
        if (out.getTargetRef() instanceof Property) {
            String sourceName = ((DataOutput) out.getSourceRef().get(0)).getName();
            return new OutputAssignmentReader(sourceName, (Property) out.getTargetRef());
        }

        if (!out.getAssignment().isEmpty() && out.getTargetRef() instanceof DataOutput) {
            String targetName = ((DataOutput) out.getTargetRef()).getName();
            return new OutputAssignmentReader(out.getAssignment().get(0), targetName);
        }

        return null;
    }

    OutputAssignmentReader(String sourceName, Property target) {
        String propertyName = getPropertyName(target);
        this.associationDeclaration = new AssociationDeclaration(
                AssociationDeclaration.Direction.Output,
                AssociationDeclaration.Type.SourceTarget,
                sourceName,
                propertyName);
    }

    OutputAssignmentReader(Assignment assignment, String targetName) {
        FormalExpression to = (FormalExpression) assignment.getTo();
        String body = FormalExpressionBodyHandler.of(to).getBody();
        String encodedBody = encode(body);
        this.associationDeclaration = new AssociationDeclaration(
                AssociationDeclaration.Direction.Output,
                AssociationDeclaration.Type.FromTo,
                targetName,
                encodedBody);
    }

    public AssociationDeclaration getAssociationDeclaration() {
        return associationDeclaration;
    }

    // fallback to ID for https://issues.jboss.org/browse/JBPM-6708
    private static String getPropertyName(Property prop) {
        return prop.getName() == null ? prop.getId() : prop.getName();
    }

    private String encode(String body) {
        return Optional
                .ofNullable(body)
                .filter(b -> !"null".equals(b))
                .map(URL::encode)
                .orElse("");
    }
}
