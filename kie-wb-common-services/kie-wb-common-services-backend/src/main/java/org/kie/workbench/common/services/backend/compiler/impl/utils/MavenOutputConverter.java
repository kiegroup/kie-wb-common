/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.services.backend.compiler.impl.utils;

import java.util.*;

import org.guvnor.common.services.project.builder.model.BuildMessage;
import org.guvnor.common.services.project.builder.model.BuildResults;
import org.guvnor.common.services.project.builder.model.IncrementalBuildResults;
import org.guvnor.common.services.shared.message.Level;
import org.guvnor.common.services.shared.validation.model.ValidationMessage;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.java.nio.file.Path;

/**
 * Maven output converter
 */
public class MavenOutputConverter {

    private static String errorLineCheck = ":[";

    public static List<ValidationMessage> convertIntoValidationMessage(List<String> mavenOutput,
                                                                       String filter, Path path, String partToCut) {
        if (mavenOutput.size() > 0) {
            Set<ValidationMessage> inserted = new TreeSet<>();
            for (String item : mavenOutput) {
                if (item.contains(filter)) {
                    ValidationMessage msg;
                    if(item.contains( errorLineCheck)) {
                         msg = getValidationMessage(path, partToCut, item);
                    }else{
                        msg = getValidationMessageWithoutLineAndColumn(partToCut, item);
                    }
                    if (!inserted.contains(msg)) {
                        inserted.add(msg);
                    }
                }
            }
            List<ValidationMessage> validationMsgs = new ArrayList<>(inserted.size());
            validationMsgs.addAll(inserted);
            return validationMsgs;
        }
        return Collections.emptyList();
    }

    public static BuildResults convertIntoBuildResults(List<String> mavenOutput,
                                                                       String filter, Path path, String partToCut) {

        if (mavenOutput.size() > 0) {
            BuildResults buildRs = new BuildResults();
            Set<BuildMessage> inserted = getErrorMsgs(mavenOutput, filter, path, partToCut);
            buildRs.addAllBuildMessages(new ArrayList<>(inserted));
            return buildRs;
        }
        return new BuildResults();
    }

    private static Set<BuildMessage> getErrorMsgs(List<String> mavenOutput, String filter, Path path, String partToCut) {
        Set<BuildMessage> inserted = new TreeSet<>();
        for (String item : mavenOutput) {
            if (item.contains(filter)) {
                BuildMessage msg ;
                if(item.contains(errorLineCheck)){
                    msg = getBuildMessage(path, partToCut, item);
                }else {
                    msg = getBuildMessageWithoutLineAndColumn(partToCut, item);
                }
                if(!inserted.contains(msg)){
                    inserted.add(msg);
                }
            }
        }
        return inserted;
    }

    private static ValidationMessage getValidationMessage(Path path, String partToCut, String item) {
        ValidationMessage msg = new ValidationMessage();
        String purged = item.replace(partToCut, "");
        int indexOfEdnFilePath = purged.lastIndexOf(errorLineCheck);
        int indexStartOf = purged.indexOf("src/");
        String errorLine = purged.substring(indexOfEdnFilePath + 2, purged.lastIndexOf("]"));
        String[] lineAndColum = getErrorLineAndColumn(errorLine);
        if (lineAndColum != null) {
            msg.setLine(Integer.parseInt(lineAndColum[0]));
            msg.setColumn(Integer.parseInt(lineAndColum[1]));
        }
        msg.setText(purged.substring(purged.lastIndexOf("]")+1));
        String pathString = purged.substring(indexStartOf, indexOfEdnFilePath);
        msg.setPath(Paths.convert(path.resolve(pathString)));
        msg.setLevel(Level.ERROR );
        return msg;
    }

    private static ValidationMessage getValidationMessageWithoutLineAndColumn(String partToCut, String item) {
        ValidationMessage msg = new ValidationMessage();
        String purged = item.replace(partToCut, "");
        msg.setText(purged);
        msg.setLevel(Level.ERROR );
        return msg;
    }


    private static BuildMessage getBuildMessage(Path path, String partToCut, String item) {
        BuildMessage msg = new BuildMessage();
        String purged = item.replace(partToCut, "");
        int indexOfEdnFilePath = purged.lastIndexOf(errorLineCheck);
        int indexStartOf = purged.indexOf("src/");
        String errorLine = purged.substring(indexOfEdnFilePath+2, purged.lastIndexOf("]"));
        String[] lineAndColum = getErrorLineAndColumn(errorLine);
        if(lineAndColum != null){
            msg.setLine(Integer.parseInt(lineAndColum[0]));
            msg.setColumn(Integer.parseInt(lineAndColum[1]));
        }
        msg.setText(purged.substring(purged.lastIndexOf("]")+1));
        String pathString = purged.substring(indexStartOf, indexOfEdnFilePath);
        msg.setPath(Paths.convert(path.resolve(pathString)));
        msg.setLevel(Level.ERROR );
        return msg;
    }

    private static BuildMessage getBuildMessageWithoutLineAndColumn(String partToCut, String item) {
        BuildMessage msg = new BuildMessage();
        String purged = item.replace(partToCut, "");
        msg.setText(purged);
        msg.setLevel(Level.ERROR );
        return msg;
    }

    private static String[] getErrorLineAndColumn(String errorLine){
        String[] lineAndColum = null;
        if(errorLine.contains(",")){
            lineAndColum = errorLine.split(",");
        }else if(errorLine.contains(":")){
            lineAndColum = errorLine.split(":");
        }
        return lineAndColum;
    }


    public static BuildResults convertIntoBuildResults(List<String> mavenOutput) {
        BuildResults buildRs = new BuildResults();
        if (mavenOutput.size() > 0) {
            for (String item : mavenOutput) {
                BuildMessage msg = new BuildMessage();
                msg.setText(item);
                buildRs.addBuildMessage(msg);
            }
        }
        return buildRs;
    }

    public static IncrementalBuildResults convertIntoIncrementalBuildResults(List<String> mavenOutput,
                                                                             String filter, Path path, String partToCut) {
        IncrementalBuildResults incrmBuildRes = new IncrementalBuildResults();
        if (mavenOutput.size() > 0) {
            Set<BuildMessage> inserted = getErrorMsgs(mavenOutput, filter, path, partToCut);
            incrmBuildRes.addAllAddedMessages(new ArrayList<>(inserted));
        }
        return incrmBuildRes;
    }
}
