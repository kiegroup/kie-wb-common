/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.workbench.common.screens.explorer.client.widgets.navigator;

import java.util.List;

import com.github.gwtbootstrap.client.ui.NavLink;
import com.github.gwtbootstrap.client.ui.base.InlineLabel;
import com.github.gwtbootstrap.client.ui.base.ListItem;
import com.github.gwtbootstrap.client.ui.constants.Constants;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import org.guvnor.structure.client.resources.NavigatorResources;
import org.kie.workbench.common.screens.explorer.client.widgets.dropdown.CustomDropdown;
import org.kie.workbench.common.screens.explorer.model.FolderItem;
import org.uberfire.ext.widgets.common.client.common.UberBreadcrumbs;
import org.uberfire.mvp.ParameterizedCommand;

public class NavigatorBreadcrumbs extends Composite {

    public static enum Mode {
        REGULAR, HEADER, SECOND_LEVEL
    }

    private final UberBreadcrumbs breadcrumbs = new UberBreadcrumbs();

    public NavigatorBreadcrumbs() {
        this( Mode.REGULAR );
    }

    public NavigatorBreadcrumbs( final Mode mode ) {
        initWidget( breadcrumbs );
        breadcrumbs.getElement().getStyle().setProperty( "whiteSpace", "nowrap" );
        if ( mode != null ) {
            switch ( mode ) {
                case HEADER:
                    breadcrumbs.removeStyleName( Constants.BREADCRUMB );
                    breadcrumbs.setStyleName( NavigatorResources.INSTANCE.css().breadcrumb() );
                    break;
                case SECOND_LEVEL:
                    breadcrumbs.addStyleName( NavigatorResources.INSTANCE.css().breadcrumb2ndLevel() );
                    break;
            }
        }
    }

    public void build( final List<FolderItem> segments,
                       final FolderItem file,
                       final ParameterizedCommand<FolderItem> onPathClick,
                       final CustomDropdown... headers ) {

        build( headers );

        if ( segments != null ) {
            for ( final FolderItem activeItem : segments ) {
                breadcrumbs.add( new NavLink( activeItem.getFileName() ) {{
                    setStyleName( NavigatorResources.INSTANCE.css().directory() );
                    addClickHandler( new ClickHandler() {
                        @Override
                        public void onClick( ClickEvent event ) {
                            onPathClick.execute( activeItem );
                        }
                    } );
                }} );
            }
            if ( file != null ) {
                breadcrumbs.add( new ListItem( new InlineLabel( file.getFileName() ) ) {{
                    setStyleName( NavigatorResources.INSTANCE.css().directory() );
                }} );
            }
        }
    }

    public void build( final CustomDropdown... headers ) {
        breadcrumbs.clear();

        for ( int i = 0; i < headers.length; i++ ) {
            final CustomDropdown header = headers[ i ];
            header.addStyleName( NavigatorResources.INSTANCE.css().breadcrumbHeader() );
            if ( i + 1 == headers.length ) {
                header.setRightDropdown( true );

                //This is a workaround for https://bugzilla.redhat.com/show_bug.cgi?id=1277556
                //When the CustomDropdown is made visible check whether it's element is clipped
                //on the left of the screen. If it is re-position it accordingly.
                header.addClickHandler( new ClickHandler() {
                    @Override
                    public void onClick( ClickEvent event ) {
                        //We have to use a DeferredCommand as there's no other event to which
                        //we can attach. The CustomDropdown's "menu" (a <UL>) is always attached
                        //to the DOM and made visible by jQuery. This seemed the simplest solution.
                        Scheduler.get().scheduleDeferred( new Command() {
                            @Override
                            public void execute() {
                                final com.google.gwt.dom.client.Element e = header.getMenuWiget().getElement();
                                final int left = e.getAbsoluteLeft();
                                if ( left < 0 ) {
                                    header.getMenuWiget().getElement().getStyle().setRight( left, Style.Unit.PX );
                                }
                            }
                        } );
                    }
                } );
            }
            breadcrumbs.add( header );
        }
    }

    @Override
    public void setVisible( boolean visible ) {
        breadcrumbs.setVisible( visible );
    }
}

