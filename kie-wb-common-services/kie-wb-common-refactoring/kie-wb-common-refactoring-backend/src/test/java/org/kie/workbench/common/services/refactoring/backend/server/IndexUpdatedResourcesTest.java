/*
 * Copyright 2014 JBoss, by Red Hat, Inc
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

package org.kie.workbench.common.services.refactoring.backend.server;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopScoreDocCollector;
import org.junit.Test;
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.uberfire.ext.metadata.backend.lucene.index.LuceneIndex;
import org.uberfire.ext.metadata.engine.Index;
import org.uberfire.ext.metadata.io.KObjectUtil;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class IndexUpdatedResourcesTest extends BaseIndexingTest {

    @Test
    public void testIndexingUpdatedResources() throws IOException, InterruptedException {
        //Add test files
        loadProperties( "file1.properties",
                        basePath );
        loadProperties( "file2.properties",
                        basePath );
        loadProperties( "file3.properties",
                        basePath );
        loadProperties( "file4.properties",
                        basePath );

        Thread.sleep( 5000 ); //wait for events to be consumed from jgit -> (notify changes -> watcher -> index) -> lucene index

        final Index index = getConfig().getIndexManager().get( KObjectUtil.toKCluster( basePath.getFileSystem() ) );

        searchFor(index,
                  new TermQuery( new Term( "title", "lucene" ) ),
                  2);

        //Update one of the files returned by the previous search, removing the "lucene" title
        final Properties properties = new Properties();
        ioService().write( basePath.resolve( "file1.properties" ),
                           propertiesToString( properties ) );

        Thread.sleep( 5000 ); //wait for events to be consumed from jgit -> (notify changes -> watcher -> index) -> lucene index

        searchFor(index,
                  new TermQuery( new Term( "title", "lucene" ) ),
                  1);
    }

    @Override
    protected TestIndexer getIndexer() {
        return new TestPropertiesFileIndexer();
    }

    @Override
    public Map<String, Analyzer> getAnalyzers() {
        return Collections.EMPTY_MAP;
    }

    @Override
    protected TestPropertiesFileTypeDefinition getResourceTypeDefinition() {
        return new TestPropertiesFileTypeDefinition();
    }

    @Override
    protected String getRepositoryName() {
        return this.getClass().getSimpleName();
    }

    @Override
    protected KieProjectService getProjectService() {
        return mock( KieProjectService.class );
    }

}
