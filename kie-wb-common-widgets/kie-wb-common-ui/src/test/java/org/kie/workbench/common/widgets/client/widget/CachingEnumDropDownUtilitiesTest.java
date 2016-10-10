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

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.models.datamodel.oracle.DropDownData;
import org.gwtbootstrap3.client.ui.ListBox;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.uberfire.backend.vfs.Path;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class CachingEnumDropDownUtilitiesTest {

    @Mock
    Path path;

    @Mock
    ListBox listBox1;

    @Mock
    ListBox listBox2;

    @Captor
    private ArgumentCaptor<String[]> listBox1ItemsCaptor;

    @Captor
    private ArgumentCaptor<String[]> listBox2ItemsCaptor;

    private CachingEnumDropDownUtilities enumDropDownUtilities;

    private String[] response;

    @Before
    public void setup() {
        CachingEnumDropDownUtilities.enumCache.clear();
        CachingEnumDropDownUtilities.pendingListBoxesCache.clear();
    }

    private void setupCachingEnumDropDownUtilities( final boolean async ) {
        final CachingEnumDropDownUtilities wrapped = new CachingEnumDropDownUtilities() {
            @Override
            void loadEnumsFromServer( final String key,
                                      final String value,
                                      final DropDownData dropData,
                                      final boolean isMultipleSelect,
                                      final Path resource,
                                      final ListBox listBox ) {
                if ( !async ) {
                    enumsLoadedFromServer( key,
                                           value,
                                           isMultipleSelect,
                                           listBox,
                                           response );
                }
            }

        };
        this.enumDropDownUtilities = spy( wrapped );
    }

    @Test
    public void checkNullDefinition() {
        setupCachingEnumDropDownUtilities( false );

        enumDropDownUtilities.setDropDownData( "",
                                               null,
                                               false,
                                               path,
                                               listBox1 );

        verify( enumDropDownUtilities,
                times( 1 ) ).fillDropDown( eq( "" ),
                                           listBox1ItemsCaptor.capture(),
                                           eq( false ),
                                           eq( listBox1 ) );

        final String[] listBoxItems = listBox1ItemsCaptor.getValue();
        assertEquals( 0,
                      listBoxItems.length );
    }

    @Test
    public void checkEmptyDefinition() {
        setupCachingEnumDropDownUtilities( false );

        enumDropDownUtilities.setDropDownData( "",
                                               DropDownData.create( new String[]{} ),
                                               false,
                                               path,
                                               listBox1 );

        verify( enumDropDownUtilities,
                times( 1 ) ).fillDropDown( eq( "" ),
                                           listBox1ItemsCaptor.capture(),
                                           eq( false ),
                                           eq( listBox1 ) );

        final String[] listBoxItems = listBox1ItemsCaptor.getValue();
        assertEquals( 0,
                      listBoxItems.length );
    }

    @Test
    public void checkFixedListDefinitionWithCaching() {
        setupCachingEnumDropDownUtilities( false );

        enumDropDownUtilities.setDropDownData( "",
                                               DropDownData.create( new String[]{ "one" } ),
                                               false,
                                               path,
                                               listBox1 );

        enumDropDownUtilities.setDropDownData( "",
                                               DropDownData.create( new String[]{ "one" } ),
                                               false,
                                               path,
                                               listBox2 );

        verify( enumDropDownUtilities,
                times( 1 ) ).fillDropDown( eq( "" ),
                                           listBox1ItemsCaptor.capture(),
                                           eq( false ),
                                           eq( listBox1 ) );
        verify( enumDropDownUtilities,
                times( 1 ) ).fillDropDown( eq( "" ),
                                           listBox2ItemsCaptor.capture(),
                                           eq( false ),
                                           eq( listBox2 ) );

        final String[] listBox1Items = listBox1ItemsCaptor.getValue();
        assertEquals( 1,
                      listBox1Items.length );
        final String[] listBox2Items = listBox2ItemsCaptor.getValue();
        assertEquals( 1,
                      listBox2Items.length );

        assertEquals( 1,
                      CachingEnumDropDownUtilities.enumCache.size() );
    }

    @Test
    public void checkQueryExpressionDefinitionWithCaching() {
        setupCachingEnumDropDownUtilities( false );

        response = new String[]{ "server-one" };
        enumDropDownUtilities.setDropDownData( "",
                                               DropDownData.create( "query", new String[]{ "one" } ),
                                               false,
                                               path,
                                               listBox1 );
        enumDropDownUtilities.setDropDownData( "",
                                               DropDownData.create( "query", new String[]{ "one" } ),
                                               false,
                                               path,
                                               listBox2 );

        verify( enumDropDownUtilities,
                times( 1 ) ).fillDropDown( eq( "" ),
                                           listBox1ItemsCaptor.capture(),
                                           eq( false ),
                                           eq( listBox1 ) );
        verify( enumDropDownUtilities,
                times( 1 ) ).fillDropDown( eq( "" ),
                                           listBox2ItemsCaptor.capture(),
                                           eq( false ),
                                           eq( listBox2 ) );

        verify( enumDropDownUtilities,
                times( 1 ) ).loadEnumsFromServer( any( String.class ),
                                                  any( String.class ),
                                                  any( DropDownData.class ),
                                                  any( Boolean.class ),
                                                  any( Path.class ),
                                                  any( ListBox.class ) );

        final String[] listBox1Items = listBox1ItemsCaptor.getValue();
        assertEquals( 1,
                      listBox1Items.length );
        final String[] listBox2Items = listBox2ItemsCaptor.getValue();
        assertEquals( 1,
                      listBox2Items.length );

        assertEquals( 1,
                      CachingEnumDropDownUtilities.enumCache.size() );
        assertEquals( 0,
                      CachingEnumDropDownUtilities.pendingListBoxesCache.size() );
    }

    @Test
    public void checkQueryExpressionDefinitionWithCachingSlowAsyncServerCalls() {
        setupCachingEnumDropDownUtilities( true );

        response = new String[]{ "server-one" };
        enumDropDownUtilities.setDropDownData( "",
                                               DropDownData.create( "query", new String[]{ "one" } ),
                                               false,
                                               path,
                                               listBox1 );
        enumDropDownUtilities.setDropDownData( "",
                                               DropDownData.create( "query", new String[]{ "one" } ),
                                               false,
                                               path,
                                               listBox2 );

        final ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass( String.class );

        verify( enumDropDownUtilities,
                times( 1 ) ).loadEnumsFromServer( keyCaptor.capture(),
                                                  any( String.class ),
                                                  any( DropDownData.class ),
                                                  any( Boolean.class ),
                                                  any( Path.class ),
                                                  any( ListBox.class ) );

        //Emulate server call completing after request for multiple ListBoxes
        final String key = keyCaptor.getValue();
        enumDropDownUtilities.enumsLoadedFromServer( key,
                                                     "",
                                                     false,
                                                     listBox1,
                                                     response );

        verify( enumDropDownUtilities,
                times( 1 ) ).fillDropDown( eq( "" ),
                                           listBox1ItemsCaptor.capture(),
                                           eq( false ),
                                           eq( listBox1 ) );
        verify( enumDropDownUtilities,
                times( 1 ) ).fillDropDown( eq( "" ),
                                           listBox2ItemsCaptor.capture(),
                                           eq( false ),
                                           eq( listBox2 ) );

        final String[] listBox1Items = listBox1ItemsCaptor.getValue();
        assertEquals( 1,
                      listBox1Items.length );
        final String[] listBox2Items = listBox2ItemsCaptor.getValue();
        assertEquals( 1,
                      listBox2Items.length );

        assertEquals( 1,
                      CachingEnumDropDownUtilities.enumCache.size() );
        assertEquals( 0,
                      CachingEnumDropDownUtilities.pendingListBoxesCache.size() );
    }

}
