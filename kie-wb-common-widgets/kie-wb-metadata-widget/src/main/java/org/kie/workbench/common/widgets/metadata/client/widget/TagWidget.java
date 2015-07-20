/*
 * Copyright 2005 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.widgets.metadata.client.widget;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.ButtonGroup;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.constants.ButtonType;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.github.gwtbootstrap.client.ui.resources.ButtonSize;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.kie.workbench.common.widgets.metadata.client.resources.Images;
import org.kie.workbench.common.widgets.metadata.client.resources.i18n.MetadataConstants;
import org.uberfire.ext.widgets.common.client.common.SmallLabel;

/**
 * This is a viewer/selector for tags.
 * It will show a list of tags currently applicable, and allow you to
 * remove/add to them.
 * <p/>
 * It is intended to work with the meta data form.
 */
public class TagWidget
        extends Composite {

    private Metadata data;

    @UiField
    HorizontalPanel tags = new HorizontalPanel();

    @UiField
    TextBox newTags = new TextBox();

    @UiField
    Button addTags = new Button(  );

    private boolean readOnly;

    interface TagWidgetBinder
            extends
            UiBinder<Widget, TagWidget> {

    }

    private static TagWidgetBinder uiBinder = GWT.create( TagWidgetBinder.class );

    public TagWidget() {
        initWidget( uiBinder.createAndBindUi( this ) );
    }

    /**
     * @param d The meta data.
     * @param readOnly If it is to be non editable.
     */
    public void setContent( Metadata d,
                            boolean readOnly ) {
        this.data = d;

        tags.clear();

        this.readOnly = readOnly;

        loadData();

        if (readOnly) {
            addTags.setVisible( false );
        } else  {
            addTags.addClickHandler( new ClickHandler() {
                @Override
                public void onClick( ClickEvent clickEvent ) {
                    String text = newTags.getText();
                    if (text != null) {
                        String[] tags = text.split( " " );
                        for (String tag : tags) {
                            addTag( tag );
                        }
                        newTags.setText( "" );
                    }
                }
            } );
        }

    }

    protected void removeTag( int index ) {
        data.removeTag( index );
        resetBox();
    }

    private void resetBox() {
        tags.clear();
        loadData();
    }

    private void loadData( ) {

        for ( int i = 0; i < data.getTags().size(); i++ ) {
            final int idx = i;
            final String tag = data.getTags().get( idx );

            Button fullTag = new Button( tag, IconType.TRASH );

            fullTag.setType( ButtonType.INVERSE );
            fullTag.setSize( ButtonSize.SMALL );


            tags.add( fullTag );
            if ( !readOnly ) {

                fullTag.addClickHandler( new ClickHandler() {
                    public void onClick( final ClickEvent event ) {
                        removeTag( idx );
                    }
                } );
            }
        }
    }

    /**
     * Appy the change (selected tag to be added).
     */
    public void addTag( String tag ) {
        if (data.getTags().contains( tag ) || tag.isEmpty()) return;
        data.addTag( tag );
        resetBox();
    }
}
