/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.services.backend.allowlist;

import java.util.HashMap;

import org.guvnor.common.services.backend.metadata.MetadataServerSideService;
import org.guvnor.common.services.backend.util.CommentedOptionFactory;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.guvnor.test.TestFileSystem;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.shared.allowlist.AllowList;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.base.options.CommentedOption;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith( MockitoJUnitRunner.class )
public class PackageNameAllowListSaverTest {

    @Mock
    private IOService ioService;

    @Mock
    private MetadataServerSideService metadataService;

    @Mock
    private CommentedOptionFactory commentedOptionFactory;

    private PackageNameAllowListSaver saver;
    private TestFileSystem testFileSystem;

    @Before
    public void setUp() throws Exception {

        testFileSystem = new TestFileSystem();

        saver = new PackageNameAllowListSaver( ioService,
                                               metadataService,
                                               commentedOptionFactory );
    }

    @After
    public void tearDown() throws Exception {
        testFileSystem.tearDown();
    }

    @Test
    public void testSave() throws Exception {

        final Path path = testFileSystem.createTempFile( "allowlist" );
        final AllowList allowList = new AllowList();
        allowList.add( "org.drools" );
        allowList.add( "org.guvnor" );
        final Metadata metadata = new Metadata();
        final String comment = "comment";

        final HashMap<String, Object> attributes = new HashMap<String, Object>();
        when( metadataService.setUpAttributes( path, metadata ) ).thenReturn( attributes );
        final CommentedOption commentedOption = mock( CommentedOption.class );
        when( commentedOptionFactory.makeCommentedOption( "comment" ) ).thenReturn( commentedOption );

        saver.save( path,
                    allowList,
                    metadata,
                    comment );

        ArgumentCaptor<String> allowListTextArgumentCaptor = ArgumentCaptor.forClass( String.class );

        verify( ioService ).write( any( org.uberfire.java.nio.file.Path.class ),
                                   allowListTextArgumentCaptor.capture(),
                                   eq( attributes ),
                                   eq( commentedOption ) );

        final String allowListAsText = allowListTextArgumentCaptor.getValue();

        assertTrue( allowListAsText.contains( "org.drools" ) );
        assertTrue( allowListAsText.contains( "org.guvnor" ) );

    }
}