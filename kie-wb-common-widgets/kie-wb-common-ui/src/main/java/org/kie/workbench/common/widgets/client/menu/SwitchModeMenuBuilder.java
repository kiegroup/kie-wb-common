package org.kie.workbench.common.widgets.client.menu;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.uberfire.client.mvp.PerspectiveActivity;
import org.uberfire.client.mvp.PerspectiveManager;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.MenuFactory.CustomMenuBuilder;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.MenuPosition;
import org.uberfire.workbench.model.menu.impl.BaseMenuCustom;
import org.uberfire.mvp.impl.ForcedPlaceRequest;

import com.github.gwtbootstrap.client.ui.NavLink;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.IsWidget;
import org.kie.workbench.common.widgets.client.mode.ContextualSwtichMode;
import static org.kie.workbench.common.widgets.client.mode.ContextualSwtichMode.ADVANCED_MODE;
import static org.kie.workbench.common.widgets.client.mode.ContextualSwtichMode.BASIC_MODE;

@ApplicationScoped
public class SwitchModeMenuBuilder  implements MenuFactory.CustomMenuBuilder {
	
    @Inject
    private PerspectiveManager perspectiveManager;

    @Inject
    private PlaceManager placeManager;

    @Inject 
    private ContextualSwtichMode contextualSwtichMode;
    
    private NavLink link = new NavLink();
    
    private CommonConstants constants = GWT.create( CommonConstants.class );
    
    public SwitchModeMenuBuilder(){
    	link.setText(constants.Basic());
        link.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
            	
            	String information = constants.SwitchModeOfPerspectives(constants.Basic());
            	if (contextualSwtichMode.getModeName().equals(BASIC_MODE)){
            			information = constants.SwitchModeOfPerspectives(constants.Advanced());
            		}
            	
                if ( Window.confirm( information ) ) {
                	final PerspectiveActivity currentPerspective = perspectiveManager.getCurrentPerspective();
                    perspectiveManager.removePerspectiveStates( new Command() {
                        @Override
                        public void execute() {
                            if ( currentPerspective != null ) {
                            	String modeName= contextualSwtichMode.getModeName();
                            	if (modeName.equals(BASIC_MODE)){
                            		modeName= ADVANCED_MODE;
                            		contextualSwtichMode.setModeName(modeName);
                            		link.setText(constants.Advanced());
                            	}else{
                            		modeName= BASIC_MODE;
                            		contextualSwtichMode.setModeName(modeName);
                            		link.setText(constants.Basic());
                            	}
                                final PlaceRequest pr = new ForcedPlaceRequest( currentPerspective.getIdentifier(),
                                		                                        currentPerspective.getPlace().getParameters());
                                placeManager.goTo( pr );
                            }
                        }
                    } );
                }
            }
        } );
    }
	@Override
	public void push(CustomMenuBuilder element) {
		 //Do nothing
		
	}

	@Override
    public MenuItem build() {
        return new BaseMenuCustom<IsWidget>() {
            @Override
            public IsWidget build() {
                return link;
            }

            @Override
            public MenuPosition getPosition() {
                return MenuPosition.RIGHT;
            }
        };
    }
}
