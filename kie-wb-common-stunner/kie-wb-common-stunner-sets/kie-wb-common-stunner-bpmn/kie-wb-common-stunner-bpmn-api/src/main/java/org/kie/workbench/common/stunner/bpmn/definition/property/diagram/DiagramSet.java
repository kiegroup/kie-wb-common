/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.definition.property.diagram;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.validation.Valid;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.forms.adf.definitions.annotations.FieldParam;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormDefinition;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormField;
import org.kie.workbench.common.forms.adf.definitions.annotations.field.selector.SelectorDataProvider;
import org.kie.workbench.common.forms.adf.definitions.settings.FieldPolicy;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.listBox.type.ListBoxFieldType;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textArea.type.TextAreaFieldType;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNViewDefinition;
import org.kie.workbench.common.stunner.bpmn.definition.UserTask;
import org.kie.workbench.common.stunner.bpmn.definition.dto.drools.ExtensionElement;
import org.kie.workbench.common.stunner.bpmn.definition.dto.drools.Global;
import org.kie.workbench.common.stunner.bpmn.definition.dto.drools.Import;
import org.kie.workbench.common.stunner.bpmn.definition.dto.drools.MetaData;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.imports.Imports;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Documentation;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Name;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.SLADueDate;
import org.kie.workbench.common.stunner.bpmn.forms.model.ImportsFieldType;
import org.kie.workbench.common.stunner.core.definition.annotation.Property;
import org.kie.workbench.common.stunner.core.util.HashUtil;
import org.treblereel.gwt.jackson.api.annotation.XmlUnwrappedCollection;

@Portable
@Bindable
@FormDefinition(
        policy = FieldPolicy.ONLY_MARKED,
        startElement = "name"
)
@XmlRootElement(name = "process", namespace = "http://www.omg.org/spec/BPMN/20100524/MODEL")
public class DiagramSet implements BaseDiagramSet {

    public static final String ADHOC = "adHoc";

    @Property
    @FormField
    @Valid
    private Name name;

    @Property
    @FormField(
            type = TextAreaFieldType.class,
            afterElement = "name"
    )
    @Valid
    private Documentation documentation;

    @Property
    @FormField(
            afterElement = "documentation"
    )
    @Valid
    private Id id;

    @Property
    @FormField(
            afterElement = "id"
    )
    @Valid
    @XmlAttribute(name = "drools:packageName")
    private Package packageProperty;

    @Property
    @FormField(
            type = ListBoxFieldType.class,
            settings = {@FieldParam(name = "addEmptyOption", value = "false")},
            afterElement = "packageProperty"
    )
    @SelectorDataProvider(
            type = SelectorDataProvider.ProviderType.CLIENT,
            className = "org.kie.workbench.common.stunner.bpmn.client.dataproviders.ProcessTypeProvider"
    )
    @Valid
    private ProcessType processType;

    @Property
    @FormField(
            afterElement = "processType"
    )
    @Valid
    @XmlAttribute(name = "drools:version")
    private Version version;

    @Property
    @FormField(
            afterElement = "version"
    )
    @Valid
    @XmlAttribute(name = "drools:adHoc")
    private AdHoc adHoc;

    @Property
    @FormField(
            afterElement = ADHOC
    )
    @Valid
    @XmlTransient
    private ProcessInstanceDescription processInstanceDescription;

    @Property
    @FormField(
            afterElement = "processInstanceDescription",
            type = ImportsFieldType.class
    )
    @Valid
    @XmlTransient
    private Imports imports;

    @Property
    @FormField(
            afterElement = "imports"
    )
    @XmlAttribute(name = "isExecutable")
    private Executable executable;

    @Property
    @FormField(afterElement = "executable")
    @XmlTransient
    private SLADueDate slaDueDate;

    @XmlElementRefs({
            @XmlElementRef(name = "metaData", type = MetaData.class),
            @XmlElementRef(name = "import", type = Import.class),
            @XmlElementRef(name = "global", type = Global.class)
    })
    @XmlElement(name = "extensionElements", namespace = "http://www.omg.org/spec/BPMN/20100524/MODEL")
    private Set<ExtensionElement> extensionElements;

    @XmlUnwrappedCollection
    @XmlElement(name = "property", namespace = "http://www.omg.org/spec/BPMN/20100524/MODEL")
    private List<org.kie.workbench.common.stunner.bpmn.definition.dto.Property> properties;

    @XmlElementRefs({
            @XmlElementRef(name = "userTask", type = UserTask.class),
    })
    @XmlUnwrappedCollection
    private List<BPMNViewDefinition> definitionList;

    public DiagramSet() {
        this(new Name(),
                new Documentation(),
                new Id(),
                new Package(),
                new ProcessType(),
                new Version(),
                new AdHoc(),
                new ProcessInstanceDescription(),
                new Imports(),
                new Executable(),
                new SLADueDate());
    }

    public DiagramSet(final @MapsTo("name") Name name,
                      final @MapsTo("documentation") Documentation documentation,
                      final @MapsTo("id") Id id,
                      final @MapsTo("packageProperty") Package packageProperty,
                      final @MapsTo("processType") ProcessType processType,
                      final @MapsTo("version") Version version,
                      final @MapsTo(ADHOC) AdHoc adHoc,
                      final @MapsTo("processInstanceDescription") ProcessInstanceDescription processInstanceDescription,
                      final @MapsTo("imports") Imports imports,
                      final @MapsTo("executable") Executable executable,
                      final @MapsTo("slaDueDate") SLADueDate slaDueDate) {
        this.name = name;
        this.documentation = documentation;
        this.id = id;
        this.packageProperty = packageProperty;
        this.processType = processType;
        this.version = version;
        this.adHoc = adHoc;
        this.processInstanceDescription = processInstanceDescription;
        this.imports = imports;
        this.executable = executable;
        this.slaDueDate = slaDueDate;
    }

    public DiagramSet(final String name) {
        this(new Name(name),
                new Documentation(),
                new Id(),
                new Package(),
                new ProcessType(),
                new Version(),
                new AdHoc(),
                new ProcessInstanceDescription(),
                new Imports(),
                new Executable(),
                new SLADueDate());
    }

    @Override
    public Name getName() {
        return name;
    }

    public void setName(final Name name) {
        this.name = name;
    }

    @Override
    public Documentation getDocumentation() {
        return documentation;
    }

    public void setDocumentation(final Documentation documentation) {
        this.documentation = documentation;
    }

    @Override
    public Id getId() {
        return id;
    }

    public void setId(Id id) {
        this.id = id;
    }

    @Override
    public AdHoc getAdHoc() {
        return adHoc;
    }

    @Override
    public Package getPackageProperty() {
        return packageProperty;
    }

    public void setPackageProperty(final Package packageProperty) {
        this.packageProperty = packageProperty;
    }

    @Override
    public Version getVersion() {
        return version;
    }

    @Override
    public ProcessType getProcessType() {
        return processType;
    }

    public void setProcessType(ProcessType processType) {
        this.processType = processType;
    }

    @Override
    public ProcessInstanceDescription getProcessInstanceDescription() {
        return processInstanceDescription;
    }

    public void setProcessInstanceDescription(final ProcessInstanceDescription processInstanceDescription) {
        this.processInstanceDescription = processInstanceDescription;
    }

    @Override
    public Imports getImports() {
        return imports;
    }

    public void setImports(Imports imports) {
        this.imports = imports;
    }

    @Override
    public Executable getExecutable() {
        return executable;
    }

    public void setExecutable(final Executable executable) {
        this.executable = executable;
    }

    @Override
    public SLADueDate getSlaDueDate() {
        return slaDueDate;
    }

    public void setSlaDueDate(final SLADueDate slaDueDate) {
        this.slaDueDate = slaDueDate;
    }

    public void setVersion(final Version version) {
        this.version = version;
    }

    public void setAdHoc(final AdHoc adHoc) {
        this.adHoc = adHoc;
    }

    public List<org.kie.workbench.common.stunner.bpmn.definition.dto.Property> getProperties() {
        return properties;
    }

    public void setProperties(List<org.kie.workbench.common.stunner.bpmn.definition.dto.Property> properties) {
        this.properties = properties;
    }


    public List<BPMNViewDefinition> getDefinitionList() {
        return definitionList;
    }

    public void setDefinitionList(List<BPMNViewDefinition> definitionList) {
        this.definitionList = definitionList;
    }

    public Set<ExtensionElement> getExtensionElements() {
        return extensionElements;
    }

    public void setExtensionElements(Set<ExtensionElement> extensionElements) {
        this.extensionElements = extensionElements;
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(Objects.hashCode(name),
                Objects.hashCode(documentation),
                Objects.hashCode(id),
                Objects.hashCode(packageProperty),
                Objects.hashCode(processType),
                Objects.hashCode(version),
                Objects.hashCode(adHoc),
                Objects.hashCode(processInstanceDescription),
                Objects.hashCode(imports),
                Objects.hashCode(executable),
                Objects.hashCode(slaDueDate));
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof DiagramSet) {
            DiagramSet other = (DiagramSet) o;
            return Objects.equals(name, other.name) &&
                    Objects.equals(documentation, other.documentation) &&
                    Objects.equals(id, other.id) &&
                    Objects.equals(packageProperty, other.packageProperty) &&
                    Objects.equals(processType, other.processType) &&
                    Objects.equals(version, other.version) &&
                    Objects.equals(adHoc, other.adHoc) &&
                    Objects.equals(processInstanceDescription, other.processInstanceDescription) &&
                    Objects.equals(imports, other.imports) &&
                    Objects.equals(executable, other.executable) &&
                    Objects.equals(slaDueDate, other.slaDueDate);
        }
        return false;
    }
}
