package org.kie.workbench.common.stunner.bpmn.backend.converters.properties;

public class ScriptLanguages {
    public static String fromUri(String format) {
        if (format == null) return "java";
        switch (format) {
            case "http://www.java.com/java":
                return "java";
            case "http://www.mvel.org/2.0":
                return "mvel";
            case "http://www.javascript.com/javascript":
                return "javascript";
            default:
                return "java";
        }
    }
}
