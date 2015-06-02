package org.kie.workbench.common.widgets.client.handlers.workbench.configuration;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.uberfire.client.mvp.UberView;
import org.uberfire.commons.validation.PortablePreconditions;

@ApplicationScoped
public class WorkbenchConfigurationPresenter {

    public interface WorkbenchConfigurationView extends UberView<WorkbenchConfigurationPresenter> {

        void show();

        void hide();

        void setActiveHandler( final WorkbenchConfigurationHandler activeHandler );

        void setTitle( String title );
    }

    @Inject
    private WorkbenchConfigurationView view;

    private WorkbenchConfigurationHandler activeHandler = null;

    @PostConstruct
    private void setup() {
        view.init( this );
    }

    public void show( final WorkbenchConfigurationHandler handler ) {
        activeHandler = PortablePreconditions.checkNotNull( "handler", handler );

        activeHandler.initHandler();
        view.setActiveHandler( activeHandler );
        view.show();
        view.setTitle( getActiveHandlerDescription() );
    }

    public void complete() {
        view.hide();
    }

    private String getActiveHandlerDescription() {
        if ( activeHandler != null ) {
            return activeHandler.getDescription();
        } else {
            return "";
        }
    }
}
