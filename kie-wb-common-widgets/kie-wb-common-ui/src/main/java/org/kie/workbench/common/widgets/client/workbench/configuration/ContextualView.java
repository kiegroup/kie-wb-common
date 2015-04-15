package org.kie.workbench.common.widgets.client.workbench.configuration;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ContextualView {

    public static final String BASIC_MODE = "Basic";
    public static final String ADVANCED_MODE = "Advanced";

    public static final String PROCESS_DEFINTIONS = "process_definitions";
    public static final String PROCESS_INSTANCES = "process_instances";
    public static final String TASK_LIST = "task_list";
    public static final String ALL_PERSPECTIVES = "all_prespectives";

    private String oldCurrentLocaleLanguage = null;
    private String currentLocaleLanguage = null;
    
    private Map<String, String> perspectiveViewMode = new HashMap<String, String>();

    {
        perspectiveViewMode.put( ALL_PERSPECTIVES , BASIC_MODE );
        perspectiveViewMode.put( PROCESS_DEFINTIONS , BASIC_MODE );
        perspectiveViewMode.put( PROCESS_INSTANCES , BASIC_MODE );
        perspectiveViewMode.put( TASK_LIST , BASIC_MODE );
    }

    public ContextualView() {

    }

    public String getViewMode( String perspective ) {
        return perspectiveViewMode.get( perspective );
    }

    public void setViewMode( String perspective , String ViewMode ) {
        perspectiveViewMode.put( perspective , ViewMode );
    }

    public String getOldCurrentLocaleLanguage() {
        return oldCurrentLocaleLanguage;
    }

    public void setOldCurrentLocaleLanguage( String oldCurrentLocaleLanguage ) {
        this.oldCurrentLocaleLanguage = oldCurrentLocaleLanguage;
    }

    public String getCurrentLocaleLanguage() {
        return currentLocaleLanguage;
    }
    
    public void setCurrentLocaleLanguage( String currentLocaleLanguage ) {
        this.currentLocaleLanguage = currentLocaleLanguage;
    }
}
