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

package org.kie.workbench.common.stunner.bpmn.definition.dto.drools;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlCData;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "onEntry-script", namespace = "http://www.jboss.org/drools")
public class Script extends ExtensionElement {

    @XmlCData
    @XmlElement(name = "script", namespace = "http://www.jboss.org/drools")
    private String script;

    @XmlAttribute
    private String scriptFormat;

    public Script() {

    }

    public Script(String script, String scriptFormat) {
        this.script = script;
        this.scriptFormat = LANGUAGE.scriptLanguageToUri(scriptFormat);
    }

    public String getScriptFormat() {
        return scriptFormat;
    }

    public void setScriptFormat(String scriptFormat) {
        this.scriptFormat = scriptFormat;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    private enum LANGUAGE {
        JAVA("java", "http://www.java.com/java"),
        JAVASCRIPT("javascript", "http://www.javascript.com/javascript"),
        MVEL("mvel", "http://www.mvel.org/2.0"),
        DROOLS("drools", "http://www.jboss.org/drools/rule"),
        FEEL("feel", "http://www.omg.org/spec/FEEL/20140401");

        private final String language;

        private final String format;

        LANGUAGE(String language,
                 String format) {
            this.language = language;
            this.format = format;
        }

        public static String scriptLanguageToUri(String language) {
            if (JAVA.language().equals(language)) {
                return JAVA.format();
            } else if (MVEL.language().equals(language)) {
                return MVEL.format();
            } else if (JAVASCRIPT.language().equals(language)) {
                return JAVASCRIPT.format();
            } else if (DROOLS.language().equals(language)) {
                return DROOLS.format();
            } else if (FEEL.language().equals(language)) {
                return FEEL.format();
            } else {
                return JAVA.language();
            }
        }

        public String language() {
            return language;
        }

        public String format() {
            return format;
        }
    }

}
