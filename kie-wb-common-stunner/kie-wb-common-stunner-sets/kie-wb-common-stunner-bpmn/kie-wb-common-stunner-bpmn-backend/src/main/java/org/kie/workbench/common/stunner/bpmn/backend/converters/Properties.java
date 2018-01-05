package org.kie.workbench.common.stunner.bpmn.backend.converters;

import com.fasterxml.jackson.core.JsonGenerator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.eclipse.bpmn2.*;
import org.eclipse.bpmn2.Process;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.jboss.drools.DroolsPackage;
import org.jboss.drools.GlobalType;
import org.jboss.drools.ImportType;
import org.jboss.drools.MetaDataType;
import org.kie.workbench.common.stunner.bpmn.backend.legacy.util.Utils;

import java.io.IOException;
import java.util.*;

import static org.kie.workbench.common.stunner.bpmn.backend.legacy.Bpmn2JsonPropertyIds.*;

public class Properties {

    public static Map<String, Object> makeProperties(Definitions def, Process process) {
        Map<String, Object> commonProperties = makeCommonProperties(def);
        // have to wait for process node to finish properties and stencil marshalling
        Map<String, Object> processProperties = makeProcessProperties(process);
        // process imports, custom description and globals extension elements
        Map<String, Object> importProperties = makeImportProperties(process, def);

        Map<String, Object> props = new HashMap<>();
        props.putAll(commonProperties);
        props.putAll(processProperties);
        props.putAll(importProperties);
        return props;
    }

    public static Map<String, Object> makeProcessProperties(Process process) {
        Map<String, Object> props = new HashMap<>();
        props.put(EXECUTABLE, process.isIsExecutable() + "");
        props.put(ID, process.getId());
        if (process.getDocumentation() != null && process.getDocumentation().size() > 0) {
            props.put(DOCUMENTATION, process.getDocumentation().get(0).getText());
        }
        if (process.getName() != null && process.getName().length() > 0) {
            props.put(PROCESSN, StringEscapeUtils.unescapeXml(process.getName()));
        }
        List<Property> processProperties = process.getProperties();
        if (processProperties != null && processProperties.size() > 0) {
            String propVal = "";
            for (int i = 0; i < processProperties.size(); i++) {
                Property p = processProperties.get(i);
                String pKPI = Utils.getMetaDataValue(p.getExtensionValues(),
                        "customKPI");
                propVal += p.getId();
                // check the structureRef value
                if (p.getItemSubjectRef() != null && p.getItemSubjectRef().getStructureRef() != null) {
                    propVal += ":" + p.getItemSubjectRef().getStructureRef();
                }
                if (pKPI != null && pKPI.length() > 0) {
                    propVal += ":" + pKPI;
                }
                if (i != processProperties.size() - 1) {
                    propVal += ",";
                }
            }
            props.put("vardefs",
                    propVal);
        }
        // packageName and version and adHoc are jbpm-specific extension attribute
        Iterator<FeatureMap.Entry> iter = process.getAnyAttribute().iterator();
        while (iter.hasNext()) {
            FeatureMap.Entry entry = iter.next();
            if (entry.getEStructuralFeature().getName().equals("packageName")) {
                props.put(PACKAGE, entry.getValue());
            }
            if (entry.getEStructuralFeature().getName().equals("version")) {
                props.put(VERSION, entry.getValue());
            }
            if (entry.getEStructuralFeature().getName().equals("adHoc")) {
                props.put(ADHOCPROCESS, entry.getValue());
            }
        }

        return props;
    }

    public static Map<String, Object> makeImportProperties(Process process, Definitions def) {
        Map<String, Object> props = new HashMap<>();
        String allImports = "";
        if (!process.getExtensionValues().isEmpty()) {
            String importsStr = "";
            String globalsStr = "";
            for (ExtensionAttributeValue extattrval : process.getExtensionValues()) {
                FeatureMap extensionElements = extattrval.getValue();
                @SuppressWarnings("unchecked")
                List<ImportType> importExtensions = (List<ImportType>) extensionElements
                        .get(DroolsPackage.Literals.DOCUMENT_ROOT__IMPORT,
                                true);
                @SuppressWarnings("unchecked")
                List<GlobalType> globalExtensions = (List<GlobalType>) extensionElements
                        .get(DroolsPackage.Literals.DOCUMENT_ROOT__GLOBAL,
                                true);
                List<MetaDataType> metadataExtensions = (List<MetaDataType>) extensionElements
                        .get(DroolsPackage.Literals.DOCUMENT_ROOT__META_DATA,
                                true);
                for (ImportType importType : importExtensions) {
                    importsStr += importType.getName();
                    importsStr += "|default,";
                }
                for (GlobalType globalType : globalExtensions) {
                    globalsStr += (globalType.getIdentifier() + ":" + globalType.getType());
                    globalsStr += ",";
                }
                for (MetaDataType metaType : metadataExtensions) {
                    props.put("customdescription",
                            metaType.getMetaValue());
                }
            }
            allImports += importsStr;
            if (globalsStr.length() > 0) {
                if (globalsStr.endsWith(",")) {
                    globalsStr = globalsStr.substring(0,
                            globalsStr.length() - 1);
                }
                props.put(GLOBALS,
                        globalsStr);
            }
        }
        // definitions imports (wsdl)
        List<Import> wsdlImports = def.getImports();
        if (wsdlImports != null) {
            for (Import imp : wsdlImports) {
                allImports += imp.getLocation() + "|" + imp.getNamespace() + "|wsdl,";
            }
        }
        if (allImports.endsWith(",")) {
            allImports = allImports.substring(0,
                    allImports.length() - 1);
        }
        props.put(IMPORTS,
                allImports);

        return props;

    }


    public static Map<String, Object> makeCommonProperties(Definitions def) {
        /**
         * "properties":{"name":"",
         * "documentation":"",
         * "auditing":"",
         * "monitoring":"",
         * "executable":"true",
         * "package":"com.sample",
         * "vardefs":"a,b,c,d",
         * "lanes" : "a,b,c",
         * "id":"",
         * "version":"",
         * "author":"",
         * "language":"",
         * "namespaces":"",
         * "targetnamespace":"",
         * "expressionlanguage":"",
         * "typelanguage":"",
         * "creationdate":"",
         * "modificationdate":""
         * }
         */
        Map<String, Object> props = new LinkedHashMap<>();
        props.put(NAMESPACES, "");
        //props.put("targetnamespace", def.getTargetNamespace());
        props.put(TARGETNAMESPACE, "http://www.omg.org/bpmn20");
        props.put(TYPELANGUAGE, def.getTypeLanguage());
        props.put(NAME, StringEscapeUtils.unescapeXml(def.getName()));
        props.put(ID,def.getId());
        props.put(EXPRESSIONLANGUAGE, def.getExpressionLanguage());
        // backwards compat for BZ 1048191
        putDocumentationProperty(def, props);

        return props;
    }

    public static void putDocumentationProperty(BaseElement baseElement,
                                          Map<String, Object> properties) {
        if (CollectionUtils.isNotEmpty(baseElement.getDocumentation())) {
            properties.put(DOCUMENTATION,
                    baseElement.getDocumentation().stream().filter(Objects::nonNull).map(Documentation::getText).findFirst().orElse(null));
        }
    }



    public static void convert(
            JsonGenerator generator,
            Map<String, Object> properties) throws IOException {
        generator.writeObjectFieldStart("properties");
        for (Map.Entry<String, Object> entry : properties.entrySet()) {
            generator.writeObjectField(entry.getKey(),
                    String.valueOf(entry.getValue()));
        }
        generator.writeEndObject();
    }

}
