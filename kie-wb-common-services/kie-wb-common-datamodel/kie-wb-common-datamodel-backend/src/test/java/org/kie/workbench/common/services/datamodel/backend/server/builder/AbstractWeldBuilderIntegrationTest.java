/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.services.datamodel.backend.server.builder;

import org.guvnor.common.services.project.builder.service.BuildService;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.junit.After;
import org.junit.Before;
import org.kie.workbench.common.services.backend.builder.core.LRUBuilderCache;
import org.kie.workbench.common.services.datamodel.backend.server.cache.LRUModuleDataModelOracleCache;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.java.nio.fs.file.SimpleFileSystemProvider;
import org.uberfire.java.nio.fs.jgit.JGitFileSystemProviderConfiguration;

public abstract class AbstractWeldBuilderIntegrationTest {

    protected final SimpleFileSystemProvider fs = new SimpleFileSystemProvider();

    private WeldContainer weldContainer;

    @Before
    public void setUp() throws Exception {
        // disable git and ssh daemons as they are not needed for the tests
        System.setProperty(JGitFileSystemProviderConfiguration.GIT_DAEMON_ENABLED, "false");
        System.setProperty(JGitFileSystemProviderConfiguration.GIT_SSH_ENABLED, "false");
        System.setProperty("org.uberfire.sys.repo.monitor.disabled", "true");

        weldContainer = new Weld().initialize();
        //Ensure URLs use the default:// scheme
        fs.forceAsDefault();
    }

    @After
    public void tearDown() {
        // Avoid NPE in case weld.initialize() failed
        if (weldContainer != null) {
            weldContainer.shutdown();
        }
    }

    public Paths getPaths() {
        return weldContainer.instance().select(Paths.class).get();
    }

    public KieModuleService getModuleService() {
        return weldContainer.instance().select(KieModuleService.class).get();
    }

    public BuildService getBuildService() {
        return weldContainer.instance().select(BuildService.class).get();
    }

    public LRUModuleDataModelOracleCache getLRUModuleDataModelOracleCache() {
        return weldContainer.instance().select(LRUModuleDataModelOracleCache.class).get();
    }

    public LRUBuilderCache getLRUBuilderCache() {
        return weldContainer.instance().select(LRUBuilderCache.class).get();
    }
}
