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

import java.nio.file.Path;

import org.kie.workbench.common.migration.cli.MigrationTool;
import org.kie.workbench.common.migration.cli.SystemAccess;
import org.kie.workbench.common.migration.cli.ToolConfig;
import org.kie.workbench.common.project.migration.cli.maven.PomProcessor;

public class PomMigrationTool implements MigrationTool {

    @Override
    public String getTitle() {
        return "POMs migration";
    }

    @Override
    public String getDescription() {
        return "Changes old POMs to new POMs version ( >7.7 community)";
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
        PromptPomMigrationService promptService = new PromptPomMigrationService(system, config);
        String jsonPath = promptService.promptForExternalConfiguration();
        Path niogitDir = config.getTarget();
        system.out().println("Starting POMs migration");
        String projectPath = niogitDir.toAbsolutePath().toString();
        PomProcessor processor = new PomProcessor(system);
        if (jsonPath.isEmpty()) {
            processor.processProject(projectPath);
        } else {
            processor.processProject(projectPath, jsonPath);
        }
        system.out().println("POMs migrated");
    }
}
