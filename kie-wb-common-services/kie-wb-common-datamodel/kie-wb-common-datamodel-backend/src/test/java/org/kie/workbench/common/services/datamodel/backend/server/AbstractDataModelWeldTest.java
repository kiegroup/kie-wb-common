/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.services.datamodel.backend.server;

import java.net.URISyntaxException;
import java.net.URL;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

import org.jboss.weld.environment.se.Weld;
import org.junit.After;
import org.junit.Before;
import org.kie.soup.project.datamodel.oracle.ProjectDataModelOracle;
import org.kie.workbench.common.services.datamodel.backend.server.service.DataModelService;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.java.nio.fs.file.SimpleFileSystemProvider;

abstract public class AbstractDataModelWeldTest {

    private final SimpleFileSystemProvider fs = new SimpleFileSystemProvider();
    private Weld weld;
    private BeanManager beanManager;

    @Before
    public void setUp() throws Exception {
        // disable git and ssh daemons as they are not needed for the tests
        System.setProperty("org.uberfire.nio.git.daemon.enabled", "false");
        System.setProperty("org.uberfire.nio.git.ssh.enabled", "false");
        System.setProperty("org.uberfire.sys.repo.monitor.disabled", "true");
        //Bootstrap WELD container
        weld = new Weld();
        beanManager = weld.initialize().getBeanManager();

        //Ensure URLs use the default:// scheme
        fs.forceAsDefault();
    }

    @After
    public void tearDown() {
        // beanManager will be null in case weld.initialize() failed. And if that is the case the shutdown method
        // would return NPE
        if (weld != null && beanManager != null) {
            weld.shutdown();
        }
    }

    protected ProjectDataModelOracle initializeProjectDataModelOracle(String projectResourceDirectoryPath) throws URISyntaxException {
        final Bean<?> dataModelServiceBean = beanManager.getBeans(DataModelService.class).iterator().next();
        final CreationalContext cc = beanManager.createCreationalContext(dataModelServiceBean);
        DataModelService dataModelService = (DataModelService) beanManager.getReference(dataModelServiceBean, DataModelService.class, cc);

        final URL packageUrl = this.getClass().getResource(projectResourceDirectoryPath);
        final org.uberfire.java.nio.file.Path nioPackagePath = fs.getPath(packageUrl.toURI());
        final Path packagePath = Paths.convert(nioPackagePath);

        return dataModelService.getProjectDataModel(packagePath);
    }
}
