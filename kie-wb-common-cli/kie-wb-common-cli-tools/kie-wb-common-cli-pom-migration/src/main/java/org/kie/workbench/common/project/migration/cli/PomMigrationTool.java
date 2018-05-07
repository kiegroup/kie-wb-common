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
package org.kie.workbench.common.project.migration.cli;

import java.io.File;
import java.nio.file.Path;
import java.util.Collection;

import org.apache.maven.model.Model;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.common.services.project.service.WorkspaceProjectService;
import org.guvnor.structure.repositories.Repository;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.kie.workbench.common.migration.cli.MigrationConstants;
import org.kie.workbench.common.migration.cli.MigrationSetup;
import org.kie.workbench.common.migration.cli.MigrationTool;
import org.kie.workbench.common.migration.cli.SystemAccess;
import org.kie.workbench.common.migration.cli.ToolConfig;
import org.kie.workbench.common.project.migration.cli.maven.PomEditor;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.IOException;
import org.uberfire.java.nio.file.FileVisitResult;
import org.uberfire.java.nio.file.Files;
import org.uberfire.java.nio.file.SimpleFileVisitor;
import org.uberfire.java.nio.file.attribute.BasicFileAttributes;

public class PomMigrationTool implements MigrationTool {

    private SystemAccess system;
    private ToolConfig config;
    private WeldContainer weldContainer;
    private ServiceCDIWrapper cdiWrapper;

    private String POM_DOT_XML = "pom.xml";

    @Override
    public String getTitle() {
        return "POMs migration";
    }

    @Override
    public String getDescription() {
        return "Migrates pom.xml files to format required in 7.7.x and later (community)";
    }

    @Override
    public Integer getPriority() {
        return 3;
    }

    @Override
    public boolean isSystemMigration() {
        return false;
    }

    @Override
    public void run(ToolConfig config, SystemAccess system) {

        this.config = config;
        this.system = system;

        PromptPomMigrationService promptService = new PromptPomMigrationService(system, config);
        String jsonPath = promptService.promptForExternalConfiguration();
        Path niogitDir = config.getTarget();
        system.out().println("Starting POMs migration");
        if (projectMigrationWasExecuted()) {
            try {
                MigrationSetup.configureProperties(system, niogitDir);
                weldContainer = new Weld().initialize();
                cdiWrapper = weldContainer.instance().select(ServiceCDIWrapper.class).get();
               if (systemMigrationWasExecuted()) {

                    WorkspaceProjectService service = weldContainer.instance().select(WorkspaceProjectService.class).get();

                    Collection<WorkspaceProject> projects = service.getAllWorkspaceProjects();
                    for(WorkspaceProject prj : projects){
                        processWorkspaceProject(prj, jsonPath);
                    }
                }
                String userHome = System.getProperty("user.home");
                system.out().println("End POMs migration, detailed log available on "+userHome + "/pom_migration.log");
            }finally {
                if (weldContainer != null) {
                    try {
                        cdiWrapper = null;
                        weldContainer.close();
                    } catch (Exception ex) {

                    }
                }
            }
        }
    }


    private void processWorkspaceProject(WorkspaceProject workspaceProject, String jsonPath) {
        PomEditor editor = new PomEditor(system, cdiWrapper);
        final int[] counter = {0};
        Files.walkFileTree(Paths.convert(workspaceProject.getRootPath()), new SimpleFileVisitor<org.uberfire.java.nio.file.Path>() {
            @Override
            public FileVisitResult visitFile(org.uberfire.java.nio.file.Path visitedPath, BasicFileAttributes attrs) throws IOException {

                org.uberfire.backend.vfs.Path visitedVFSPath = Paths.convert(visitedPath);
                String fileName = visitedVFSPath.getFileName();
                File file = visitedPath.toFile();

                if (file.isFile() && fileName.equals(POM_DOT_XML)){
                        try {
                            Model model;
                            if (jsonPath.isEmpty()) {
                                model = editor.updatePom(visitedPath);
                            } else {
                                model =  editor.updatePom(visitedPath, jsonPath);
                            }
                            if (!model.getBuild().getPlugins().isEmpty()) {
                                counter[0]++;
                            }
                        } catch (Exception e) {
                            system.err().println("Error reading form: " + fileName + ":\n");
                            e.printStackTrace(system.err());
                        }
                }
                return FileVisitResult.CONTINUE;
            }
        });
        system.out().println("Migrated " + counter[0] + " POMs for prj:"+workspaceProject.getName());
    }

        private boolean projectMigrationWasExecuted() {
            if (!config.getTarget().resolve("system").resolve(MigrationConstants.SYSTEM_GIT).toFile().exists()) {
                system.err().println(String.format("The PROJECT STRUCTURE MIGRATION must be ran before this one."));
                return false;
            }
            return true;
        }

    private boolean systemMigrationWasExecuted() {
        final IOService systemIoService = cdiWrapper.getSystemIoService();
        final Repository systemRepository = cdiWrapper.getSystemRepository();
        if (!systemIoService.exists(systemIoService.get(systemRepository.getUri()).resolve("spaces"))) {
            system.err().println(String.format("The SYSTEM CONFIGURATION DIRECTORY STRUCTURE MIGRATION must be ran before this one."));
            return false;
        }
        return true;
    }
}
