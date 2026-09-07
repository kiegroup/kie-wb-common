/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.screens.workbench.backend;

import java.io.File;
import java.net.URL;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import io.swagger.jaxrs.config.BeanConfig;
import org.reflections.vfs.Vfs;
import org.kie.workbench.common.screens.server.management.utils.ControllerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SwaggerAPIScanner implements ServletContextListener {

    private static final Boolean SWAGGER_DISABLED = Boolean.parseBoolean(System.getProperty("org.kie.workbench.swagger.disabled",
                                                                                            "false"));
    private static final Logger LOGGER = LoggerFactory.getLogger(SwaggerAPIScanner.class);

    @Override
    public void contextInitialized(final ServletContextEvent sce) {

        if (SWAGGER_DISABLED) {
            return;
        }

        LOGGER.info("Starting Swagger API discovery");
        
        final String warRealPath = sce.getServletContext().getRealPath("/");
        if (warRealPath != null) {
            final String libDirPath = warRealPath + File.separator
                    + "WEB-INF" + File.separator + "lib" + File.separator;
            if (new File(libDirPath).isDirectory()) {
                Vfs.addDefaultURLTypes(new Vfs.UrlType() {
                    @Override
                    public boolean matches(URL url) {
                        return "vfs".equals(url.getProtocol());
                    }
                    @Override
                    public Vfs.Dir createDir(URL url) throws Exception {
                        // EAP appends a trailing slash to jar vfs: URLs — strip it first
                        String path = url.getPath();
                        while (path.endsWith("/")) { path = path.substring(0, path.length() - 1); }
                        final File jar = new File(libDirPath + path.substring(path.lastIndexOf('/') + 1));
                        if (jar.exists() && jar.canRead()) {
                            return new org.reflections.vfs.ZipDir(new java.util.jar.JarFile(jar));
                        }
                        return new Vfs.Dir() {
                            @Override public String getPath() { return url.getPath(); }
                            @Override public Iterable<Vfs.File> getFiles() { return java.util.Collections.emptyList(); }
                            @Override public void close() { }
                        };
                    }
                });
            }
        }

        BeanConfig beanConfig = new BeanConfig();
        beanConfig.setBasePath(sce.getServletContext().getContextPath() + "/rest");
        if (ControllerUtils.useEmbeddedController()) {
            //Controller REST endpoints only available when using embedded controller
            beanConfig.setResourcePackage("org.kie.server.controller.rest");
        }
        beanConfig.setVersion("7.0");
        beanConfig.setTitle("Business Central API");
        beanConfig.setPrettyPrint(true);
        beanConfig.setScan();
    }

    @Override
    public void contextDestroyed(final ServletContextEvent sce) {
    }
}
