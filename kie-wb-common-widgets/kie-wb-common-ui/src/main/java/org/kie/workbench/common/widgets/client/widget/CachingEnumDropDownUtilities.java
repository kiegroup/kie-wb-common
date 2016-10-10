/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.widgets.client.widget;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.gwt.core.client.Scheduler;
import org.drools.workbench.models.datamodel.oracle.DropDownData;
import org.gwtbootstrap3.client.ui.ListBox;
import org.jboss.errai.bus.client.api.BusErrorCallback;
import org.jboss.errai.bus.client.api.base.MessageBuilder;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.services.shared.enums.EnumDropdownService;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.widgets.common.client.common.BusyPopup;

public class CachingEnumDropDownUtilities extends EnumDropDownUtilities {

    static final Map<String, String[]> enumCache = new HashMap<String, String[]>();
    static final Map<String, Set<ListBox>> pendingListBoxesCache = new HashMap<String, Set<ListBox>>();

    @Override
    public void setDropDownData( final String value,
                                 final DropDownData dropData,
                                 final boolean isMultipleSelect,
                                 final Path resource,
                                 final ListBox listBox ) {
        if ( dropData == null ) {
            fillDropDown( value,
                          new String[ 0 ],
                          isMultipleSelect,
                          listBox );
            return;
        }

        //Lookup data from definition if the list of enumerations comes from the definition itself
        if ( dropData.getFixedList() != null ) {
            getEnumsFromFixedList( value,
                                   dropData,
                                   isMultipleSelect,
                                   listBox );
            return;
        }

        //Lookup data from server if the list of enumerations comes from an external query
        if ( dropData.getQueryExpression() != null ) {
            getEnumsFromServer( value,
                                dropData,
                                isMultipleSelect,
                                resource,
                                listBox );
        }
    }

    @Override
    //Needs to be public for Unit Tests use of 'spy(..)'
    public void fillDropDown( final String value,
                              final DropDownData dropData,
                              final boolean isMultipleSelect,
                              final ListBox listBox ) {
        super.fillDropDown( value,
                            dropData,
                            isMultipleSelect,
                            listBox );
    }

    @Override
    //Needs to be public for Unit Tests use of 'spy(..)'
    public void fillDropDown( final String value,
                              final String[] enumeratedValues,
                              final boolean isMultipleSelect,
                              final ListBox listBox ) {
        super.fillDropDown( value,
                            enumeratedValues,
                            isMultipleSelect,
                            listBox );
    }

    private void getEnumsFromFixedList( final String value,
                                        final DropDownData dropData,
                                        final boolean isMultipleSelect,
                                        final ListBox listBox ) {
        final String key = buildKey( dropData );
        if ( !enumCache.containsKey( key ) ) {
            enumCache.put( key,
                           dropData.getFixedList() );
        }
        fillDropDown( value,
                      enumCache.get( key ),
                      isMultipleSelect,
                      listBox );
    }

    private void getEnumsFromServer( final String value,
                                     final DropDownData dropData,
                                     final boolean isMultipleSelect,
                                     final Path resource,
                                     final ListBox listBox ) {
        final String key = buildKey( dropData );
        if ( enumCache.containsKey( key ) ) {
            final String[] items = enumCache.get( key );
            if ( items.length == 0 ) {
                if ( !pendingListBoxesCache.containsKey( key ) ) {
                    pendingListBoxesCache.put( key,
                                               new HashSet<ListBox>() );
                }
                final Set<ListBox> pendingListBoxes = pendingListBoxesCache.get( key );
                pendingListBoxes.add( listBox );
                return;

            } else {
                fillDropDown( value,
                              items,
                              isMultipleSelect,
                              listBox );
                return;
            }
        }

        //Cache empty value to prevent recurrent calls to the server for the same data.
        //All list boxes that need filling on subsequent requests are cached until the
        //first request completes and then populated.
        enumCache.put( key,
                       new String[ 0 ] );

        loadEnumsFromServer( key,
                             value,
                             dropData,
                             isMultipleSelect,
                             resource,
                             listBox );
    }

    void loadEnumsFromServer( final String key,
                              final String value,
                              final DropDownData dropData,
                              final boolean isMultipleSelect,
                              final Path resource,
                              final ListBox listBox ) {
        Scheduler.get().scheduleDeferred( new com.google.gwt.user.client.Command() {
            public void execute() {
                BusyPopup.showMessage( CommonConstants.INSTANCE.RefreshingList() );

                MessageBuilder.createCall( new RemoteCallback<String[]>() {
                                               public void callback( final String[] response ) {
                                                   enumsLoadedFromServer( key,
                                                                          value,
                                                                          isMultipleSelect,
                                                                          listBox,
                                                                          response );
                                               }
                                           },
                                           new BusErrorCallback() {
                                               @Override
                                               public boolean error( Message message,
                                                                     Throwable throwable ) {
                                                   BusyPopup.close();
                                                   return false;
                                               }
                                           },
                                           EnumDropdownService.class ).loadDropDownExpression( resource,
                                                                                               dropData.getValuePairs(),
                                                                                               dropData.getQueryExpression() );
            }
        } );
    }

    void enumsLoadedFromServer( final String key,
                                final String value,
                                final boolean isMultipleSelect,
                                final ListBox listBox,
                                String[] response ) {
        BusyPopup.close();

        if ( response.length == 0 ) {
            response = new String[]{ CommonConstants.INSTANCE.UnableToLoadList() };

        } else {
            final Set<ListBox> pendingListBoxes = pendingListBoxesCache.remove( key );
            if ( !( pendingListBoxes == null || pendingListBoxes.isEmpty() ) ) {
                for ( ListBox lb : pendingListBoxes ) {
                    fillDropDown( value,
                                  response,
                                  isMultipleSelect,
                                  lb );
                }
            }
        }

        enumCache.put( key,
                       response );

        fillDropDown( value,
                      response,
                      isMultipleSelect,
                      listBox );
    }

    private String buildKey( final DropDownData enumDefinition ) {
        if ( enumDefinition.getFixedList() != null ) {
            return buildFixedListKey( enumDefinition.getFixedList() );
        } else {
            return buildQueryExpressionKey( enumDefinition.getQueryExpression(),
                                            enumDefinition.getValuePairs() );
        }
    }

    private String buildFixedListKey( final String[] items ) {
        final StringBuilder sb = new StringBuilder();
        for ( String item : items ) {
            sb.append( item ).append( "#" );
        }
        final String key = sb.toString();
        return key;
    }

    private String buildQueryExpressionKey( final String queryExpression,
                                            final String[] items ) {
        final StringBuilder sb = new StringBuilder();
        sb.append( queryExpression ).append( "#" );
        sb.append( buildFixedListKey( items ) );
        final String key = sb.toString();
        return key;
    }

}
