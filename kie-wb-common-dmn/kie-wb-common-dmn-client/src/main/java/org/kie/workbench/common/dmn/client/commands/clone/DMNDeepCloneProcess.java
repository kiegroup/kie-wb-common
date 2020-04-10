/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.dmn.client.commands.clone;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.enterprise.inject.Alternative;
import javax.inject.Inject;

import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasVariable;
import org.kie.workbench.common.dmn.api.definition.model.BusinessKnowledgeModel;
import org.kie.workbench.common.dmn.api.definition.model.DRGElement;
import org.kie.workbench.common.dmn.api.definition.model.Decision;
import org.kie.workbench.common.dmn.api.definition.model.Expression;
import org.kie.workbench.common.dmn.api.definition.model.FunctionDefinition;
import org.kie.workbench.common.dmn.api.definition.model.InformationItem;
import org.kie.workbench.common.dmn.api.property.dmn.DMNExternalLink;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.definition.adapter.AdapterManager;
import org.kie.workbench.common.stunner.core.definition.clone.DeepCloneProcess;
import org.kie.workbench.common.stunner.core.definition.clone.IDeepCloneProcess;
import org.kie.workbench.common.stunner.core.util.ClassUtils;

@Alternative
public class DMNDeepCloneProcess extends DeepCloneProcess implements IDeepCloneProcess {

    protected DMNDeepCloneProcess() {
        this(null,
             null,
             null);
    }

    @Inject
    public DMNDeepCloneProcess(final FactoryManager factoryManager,
                               final AdapterManager adapterManager,
                               final ClassUtils classUtils) {
        super(factoryManager, adapterManager, classUtils);
    }

    @Override
    public <S, T> T clone(S source,
                          T target) {
        if (source instanceof DRGElement) {
            DRGElement sourceDefinition = (DRGElement) source;
            DRGElement targetDefinition = (DRGElement) target;

            targetDefinition.getNameHolder().setValue(cloneName(sourceDefinition));
            targetDefinition.getLinksHolder().getValue().getLinks().addAll(cloneExternalLinkList(sourceDefinition));
        }

        if (source instanceof HasVariable) {
            QName srcTypeRef = ((HasVariable) source).getVariable().getTypeRef();
            ((HasVariable) target).getVariable().setTypeRef(cloneTypeRef(srcTypeRef));
        }

        if (source instanceof BusinessKnowledgeModel) {
            FunctionDefinition srcFunctionDefinition = ((BusinessKnowledgeModel) source).getEncapsulatedLogic();
            FunctionDefinition targetFunctionDefinition = cloneFunctionDefinition(srcFunctionDefinition);
            targetFunctionDefinition.setParent((BusinessKnowledgeModel) target);
            ((BusinessKnowledgeModel) target).setEncapsulatedLogic(targetFunctionDefinition);
        }

        if (source instanceof Decision) {
            ((Decision) target).setExpression(cloneExpression((Decision) source));
        }

        return super.clone(source, target);
    }

    private Name cloneName(DRGElement sourceDefinition) {
        return new Name(sourceDefinition.getNameHolder().getValue().getValue());
    }

    private QName cloneTypeRef(QName typeRef) {
        return new QName(typeRef.getNamespaceURI(), typeRef.getLocalPart(), typeRef.getPrefix());
    }

    private FunctionDefinition cloneFunctionDefinition(FunctionDefinition srcFunctionDefinition) {
        FunctionDefinition targetFunctionDefinition = new FunctionDefinition();
        targetFunctionDefinition.setId(new Id());
        targetFunctionDefinition.getHasTypeRefs().addAll(srcFunctionDefinition.getHasTypeRefs());
        targetFunctionDefinition.getFormalParameter().addAll(cloneFormalParameterList(srcFunctionDefinition));
        targetFunctionDefinition.getAdditionalAttributes().putAll(cloneAdditionalAttributes(srcFunctionDefinition));
        targetFunctionDefinition.setDescription(srcFunctionDefinition.getDescription());
        targetFunctionDefinition.setExpression(cloneExpression(srcFunctionDefinition));
        targetFunctionDefinition.setTypeRef(cloneTypeRef(srcFunctionDefinition.getTypeRef()));
        targetFunctionDefinition.setKind(srcFunctionDefinition.getKind());
        targetFunctionDefinition.setExtensionElements(srcFunctionDefinition.getExtensionElements());
        return targetFunctionDefinition;
    }

    private Map<QName, String> cloneAdditionalAttributes(FunctionDefinition srcFunctionDefinition) {
        return srcFunctionDefinition.getAdditionalAttributes().keySet()
                .stream()
                .map(this::cloneTypeRef)
                .collect(Collectors.toMap(
                        typeRef -> typeRef,
                        typeRef -> srcFunctionDefinition.getAdditionalAttributes().get(typeRef)
                ));
    }

    private List<InformationItem> cloneFormalParameterList(FunctionDefinition srcFunctionDefinition) {
        return srcFunctionDefinition.getFormalParameter()
                .stream()
                .map(informationItem -> new InformationItem(
                        new Id(),
                        informationItem.getDescription(),
                        new Name(informationItem.getName().getValue()),
                        cloneTypeRef(informationItem.getTypeRef())
                ))
                .collect(Collectors.toList());
    }

    private List<DMNExternalLink> cloneExternalLinkList(DRGElement sourceDefinition) {
        return sourceDefinition.getLinksHolder().getValue()
                .getLinks()
                .stream()
                .map(srcLink -> new DMNExternalLink(srcLink.getUrl(), srcLink.getDescription()))
                .collect(Collectors.toList());
    }

    private Expression cloneExpression(HasExpression srcFunctionDefinition) {
        // TODO Expression should be cloned as well, based on the specific real implementation
        return srcFunctionDefinition.getExpression();
    }
}
