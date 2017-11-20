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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.guvnor.common.services.project.builder.model.BuildMessage;
import org.guvnor.common.services.project.builder.model.BuildResults;
import org.guvnor.common.services.project.builder.model.IncrementalBuildResults;
import org.guvnor.common.services.shared.message.Level;
import org.guvnor.common.services.shared.validation.model.ValidationMessage;
import org.uberfire.java.nio.file.Path;

import static org.uberfire.backend.server.util.Paths.convert;

/**
 * Maven output converter
 */
public class MavenOutputConverter {

    private static Collection<InputProcessor> filters = new ArrayList<>();

    static {
        //JDT: failOnError=false
        //"2017-11-16 16:35:05,023 [Thread-2579] WARN  /private/var/folders/j4/86jpk9rx5rzdrbtrxwsfv0pr0000gn/T/maven/41643253-9e34-4768-985d-74a2f3728b7d/myrepo/mortgages/src/main/java/mortgages/mortgages/Applicant.java:[21] ",
        //"\tstatic final xxxx serialVersionUID = 1L;",
        //"\t             ^^^^",
        //"xxxx cannot be resolved to a type",
        filters.add(new BaseInputProcessor() {
            @Override
            public boolean accept(final Path rootPath,
                                  final String workingDir,
                                  final String value) {
                return value.matches(".*\\s+WARN\\s+" + workingDir + ".*");
            }

            @Override
            public Result process(final Path rootPath,
                                  final String workingDir,
                                  final List<String> allValues,
                                  final int index) {
                List<BuildMessage> result = new ArrayList<>();
                final BuildMessage msg = getBuildMessage(rootPath, workingDir, allValues.get(index));
                int j = index + 1;
                for (; j < allValues.size(); j++) {
                    final String nextItem = allValues.get(j);
                    if (nextItem.matches("\\d+\\s+problems?\\s+\\(.*")) {
                        break;
                    } else {
                        result.add(getBuildMessage(msg, nextItem));
                    }
                }

                return new Result(result, j);
            }
        });
        //JavaC: Standard
        //"2017-11-14 22:30:07,476 [Thread-2568] ERROR /private/var/folders/j4/86jpk9rx5rzdrbtrxwsfv0pr0000gn/T/maven/6fd6b697-461c-407e-965b-f799d2f9dc7e/myrepo/mortgages/src/main/java/mortgages/mortgages/Applicant.java:[21,22] cannot find symbol",
        //"  symbol:   class xxxx",
        //"  location: class mortgages.mortgages.Applicant",
        filters.add(new BaseInputProcessor() {
            @Override
            public boolean accept(final Path rootPath,
                                  final String workingDir,
                                  final String value) {
                return value.matches(".*\\s+ERROR\\s+" + workingDir + ".*");
            }

            @Override
            public Result process(final Path rootPath,
                                  final String workingDir,
                                  final List<String> allValues,
                                  final int index) {
                List<BuildMessage> result = new ArrayList<>();
                final BuildMessage msg = getBuildMessage(rootPath, workingDir, allValues.get(index));
                result.add(msg);
                int j = index + 1;
                for (; j < allValues.size(); j++) {
                    final String nextItem = allValues.get(j);
                    if (nextItem.matches("\\s+.*")) {
                        result.add(getBuildMessage(msg, nextItem));
                    } else {
                        break;
                    }
                }

                return new Result(result, j);
            }
        });
        //KIE MAVEN PLUGIN
        //"2017-11-16 16:35:09,490 [Thread-2579] ERROR Message [id=1, kieBase=defaultKieBase, level=ERROR, path=mortgages/mortgages/No bad credit checks.rdrl, line=27, column=0",
        //"   text=Unable to resolve ObjectType 'Applicant']",
        filters.add(new BaseInputProcessor() {
            @Override
            public boolean accept(final Path rootPath,
                                  final String workingDir,
                                  final String value) {
                return value.matches(".*\\s+ERROR\\s+Message\\s+\\[id=.*kieBase=.*");
            }

            @Override
            public Result process(final Path rootPath,
                                  final String workingDir,
                                  final List<String> allValues,
                                  final int index) {
                final String message = allValues.get(index);

                String level = null;
                String kiebase = null;
                String text = null;
                String path = null;
                String line = null;
                String column = null;
                List<BuildMessage> result = new ArrayList<>();
                int kbaseIdx = message.indexOf("kieBase=");
                int levelIdx = message.indexOf("level=");
                int pathIdx = message.indexOf("path=");
                int lineIdx = message.indexOf("line=");
                int columnIdx = message.indexOf("column=");
                if (kbaseIdx > 0) {
                    kiebase = message.substring(kbaseIdx + 8, nextCharIndex(message, kbaseIdx, ",")).trim();
                }
                if (levelIdx > 0) {
                    level = message.substring(levelIdx + 6, nextCharIndex(message, levelIdx, ",")).trim();
                }
                if (pathIdx > 0) {
                    path = message.substring(pathIdx + 5, nextCharIndex(message, pathIdx, ",")).trim();
                }

                if (lineIdx > 0) {
                    line = message.substring(lineIdx + 5, nextCharIndex(message, lineIdx, ",")).trim();
                }

                if (columnIdx > 0) {
                    column = message.substring(columnIdx + 7).trim();
                }

                final String messageNextLine = allValues.get(index + 1);
                int textIdx = messageNextLine.indexOf("text=");
                if (textIdx > 0) {
                    text = messageNextLine.substring(textIdx + 5, nextCharIndex(messageNextLine, textIdx, "]")).trim();
                }

                final BuildMessage msg = new BuildMessage();
                msg.setText(text);
                msg.setLevel(Level.valueOf(level));
                msg.setPath(convert(rootPath.resolve("src/main/resources/" + path)));
                msg.setLine(Integer.valueOf(line));
                msg.setColumn(Integer.valueOf(column));
                return new Result(Collections.singletonList(msg), index + 1);
            }

            private int nextCharIndex(String message, int index, String s) {
                int substringIndex = message.substring(index).indexOf(s);
                return substringIndex + index;
            }
        });
    }

    public static List<ValidationMessage> convertIntoValidationMessage(List<String> mavenOutput,
                                                                       String filter, Path path, String partToCut) {
        if (mavenOutput.size() > 0) {
            Set<ValidationMessage> inserted = new TreeSet<>();
            for (String item : mavenOutput) {
                if (item.contains(filter)) {
                    ValidationMessage msg;
////                    if (item.contains(errorLineCheck)) {
////                        msg = getValidationMessage(path, partToCut, item);
////                    } else {
////                        msg = getValidationMessageWithoutLineAndColumn(partToCut, item);
////                    }
//                    if (msg.getText() != null && !inserted.contains(msg)) {
//                        inserted.add(msg);
//                    }
                }
            }
            List<ValidationMessage> validationMsgs = new ArrayList<>(inserted.size());
            validationMsgs.addAll(inserted);
            return validationMsgs;
        }
        return Collections.emptyList();
    }

    public static BuildResults convertIntoBuildResults(final List<String> mavenOutput,
                                                       final Path path,
                                                       final String partToCut) {

        if (mavenOutput.size() > 0) {
            BuildResults buildRs = new BuildResults();
            Set<BuildMessage> inserted = getErrorMsgs(mavenOutput, path, partToCut);
            buildRs.addAllBuildMessages(new ArrayList<>(inserted));
            return buildRs;
        }
        return new BuildResults();
    }

    private static Set<BuildMessage> getErrorMsgs(final List<String> mavenOutput,
                                                  final Path path,
                                                  final String partToCut) {
        final Set<BuildMessage> inserted = new LinkedHashSet<>(mavenOutput.size());
        for (int i = 0; i < mavenOutput.size(); i++) {
            String item = mavenOutput.get(i);

            for (final InputProcessor filter : filters) {
                if (filter.accept(path, partToCut, item)) {
                    InputProcessor.Result result = filter.process(path, partToCut, mavenOutput, i);
                    inserted.addAll(result.getMessages());
                    i = result.getNewIndex();
                    break;
                }
            }
        }
        return inserted;
    }

    private static ValidationMessage getValidationMessage(Path path, String partToCut, String item) {
        ValidationMessage msg = new ValidationMessage();
        String purged = item.replace(partToCut, "");
////        int indexOfEdnFilePath = purged.lastIndexOf(errorLineCheck);
////        int indexStartOf = purged.indexOf("src/");
//        String errorLine = purged.substring(indexOfEdnFilePath + 2, purged.lastIndexOf("]"));
//        String[] lineAndColum = getErrorLineAndColumn(errorLine);
//        if (lineAndColum != null) {
//            msg.setLine(Integer.parseInt(lineAndColum[0]));
//            msg.setColumn(Integer.parseInt(lineAndColum[1]));
//        }
        msg.setText(purged.substring(purged.lastIndexOf("]") + 1));
//        String pathString = purged.substring(indexStartOf, indexOfEdnFilePath);
//        msg.setPath(Paths.convert(path.resolve(pathString)));
        msg.setLevel(Level.ERROR);
        return msg;
    }

    private static ValidationMessage getValidationMessageWithoutLineAndColumn(String partToCut, String item) {
        ValidationMessage msg = new ValidationMessage();
        String purged = item.replace(partToCut, "");
        msg.setText(purged);
        msg.setLevel(Level.ERROR);
        return msg;
    }

    private static BuildMessage getBuildMessageWithoutLineAndColumn(String partToCut, String item) {
        BuildMessage msg = new BuildMessage();
        String purged = item.replace(partToCut, "");
        msg.setText(purged);
        msg.setLevel(Level.ERROR);
        return msg;
    }

    public static BuildResults convertIntoBuildResults(final List<String> mavenOutput) {
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

    public static IncrementalBuildResults convertIntoIncrementalBuildResults(final List<String> mavenOutput,
                                                                             final Path path,
                                                                             final String partToCut) {
        IncrementalBuildResults incrmBuildRes = new IncrementalBuildResults();
        if (mavenOutput.size() > 0) {
            Set<BuildMessage> inserted = getErrorMsgs(mavenOutput, path, partToCut);
            incrmBuildRes.addAllAddedMessages(new ArrayList<>(inserted));
        }
        return incrmBuildRes;
    }
}
