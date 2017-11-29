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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.guvnor.common.services.project.builder.model.BuildResults;
import org.guvnor.common.services.shared.builder.model.BuildMessage;
import org.guvnor.common.services.shared.message.Level;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.java.nio.file.Path;

import static org.uberfire.backend.server.util.Paths.convert;

/**
 * Maven output converter
 */
public class MavenOutputConverter {

    private static Collection<InputProcessor> filters = new ArrayList<>();

    static {
        //2017-11-28 13:21:31,070 [Thread-2604] WARN  /private/var/folders/j4/86jpk9rx5rzdrbtrxwsfv0pr0000gn/T/maven/6bb64341-93ab-4e26-813d-84ee2a8c9700/myrepo/mortgages/src/main/java/mortgages/mortgages/Applicant.java:[21,18] [ERROR] xxxx cannot be resolved to a type
        filters.add(new BaseInputProcessor() {
            @Override
            public boolean accept(final Path rootPath,
                                  final String workingDir,
                                  final String value) {
                return value.matches(".*\\s+WARN\\s+" + workingDir + ".*\\s+\\[ERROR\\]\\s+.*");
            }

            @Override
            public Result process(final Path rootPath,
                                  final String workingDir,
                                  final List<String> allValues,
                                  final int index) {
                final BuildMessage msg = getBuildMessage(rootPath, workingDir, allValues.get(index));
                return new Result(Collections.singletonList(msg), index + 1);
            }

            private BuildMessage getBuildMessage(final Path rootPath,
                                                 final String workingDir,
                                                 final String item) {
                final BuildMessage msg = new BuildMessage();
                final int indexOfEdnFilePath = item.lastIndexOf(":[");
                final String fileName = item.substring(item.indexOf(workingDir) + workingDir.length(), indexOfEdnFilePath);
                final int endLineInfoIndex = indexOfEdnFilePath + item.substring(indexOfEdnFilePath).indexOf("]");
                final LineColumn lineAndColumn = getLineAndColumn(item.substring(indexOfEdnFilePath + 2, endLineInfoIndex));

                msg.setLine(Integer.parseInt(lineAndColumn.getLine()));
                if (lineAndColumn.getColumn() != null) {
                    msg.setColumn(Integer.parseInt(lineAndColumn.getColumn()));
                }
                msg.setText(item.substring(endLineInfoIndex + 1));
                msg.setPath(Paths.convert(rootPath.resolve(fileName)));
                msg.setLevel(Level.ERROR);

                return msg;
            }

            private LineColumn getLineAndColumn(final String errorLine) {
                if (errorLine.contains(",")) {
                    String[] result = errorLine.split(",");
                    return new LineColumn(result[0], result[1]);
                } else if (errorLine.contains(":")) {
                    String[] result = errorLine.split(":");
                    return new LineColumn(result[0], result[1]);
                }
                return new LineColumn(errorLine);
            }
        });
        //KIE MAVEN PLUGIN
        //"2017-11-16 16:35:09,490 [Thread-2579] ERROR Message [id=1, kieBase=defaultKieBase, level=ERROR, path=mortgages/mortgages/No bad credit checks.rdrl, line=27, column=0",
        //"   text=Unable to resolve ObjectType 'Applicant']",
        //OR
        //"2017-11-16 16:35:09,490 [Thread-2579] ERROR Message [id=5, kieBase=defaultKieBase, level=ERROR, path=mortgages/mortgages/CreditApproval.rdslr, line=5, column=0",
        //"   text=Unable to Analyse Expression applicant.setApproved(true);:",
        //"[Error: unable to resolve method using strict-mode: org.drools.core.spi.KnowledgeHelper.applicant()]",
        //"[Near : {... applicant.setApproved(true); ....}]",
        //"             ^",
        //"[Line: 5, Column: 0]",

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
                                  final int _line) {
                int currentLine = _line;
                String level = null;
                String kiebase = null;
                String path = null;
                String line = null;
                String column = null;
                BuildMessage baseMsg = null;
                final List<BuildMessage> result = new ArrayList<>();

                StringBuilder sb = new StringBuilder();
                final String content = allValues.get(currentLine);
                char[] message = content.toCharArray();
                int i = content.indexOf("Message [id=") + 9;
                int bracketsCount = 1;
                boolean gettingText = false;
                while (bracketsCount != 0) {
                    boolean append = true;
                    if (message[i] == '[') {
                        append = false;
                        if (gettingText) {
                            sb.append('[');
                        }
                        bracketsCount++;
                    } else if (message[i] == ']') {
                        bracketsCount--;
                        append = false;
                        if (bracketsCount > 0 && gettingText) {
                            sb.append(']');
                        }
                    }

                    if (checkEquals(message, i, "kieBase=")) {
                        kiebase = getValue(message, i + 8);
                        i = i + 7 + kiebase.length();
                    } else if (checkEquals(message, i, "level=")) {
                        level = getValue(message, i + 6);
                        i = i + 5 + level.length();
                    } else if (checkEquals(message, i, "path=")) {
                        path = getValue(message, i + 5);
                        i = i + 4 + path.length();
                        path = path.replace(workingDir, "");
                    } else if (checkEquals(message, i, "line=")) {
                        line = getValue(message, i + 5);
                        i = i + 4 + line.length();
                    } else if (checkEquals(message, i, "column=")) {
                        column = getValue(message, i + 7);
                        i = i + 6 + column.length();
                    } else if (checkEquals(message, i, "text=")) {
                        gettingText = true;
                        i = i + 4;
                    } else if (gettingText && append) {
                        sb.append(message[i]);
                    }
                    i++;
                    if (i >= message.length) {
                        if (baseMsg == null && sb.length() == 0) {
                            baseMsg = new BuildMessage();
                            baseMsg.setLevel(Level.valueOf(level));
                            baseMsg.setPath(convert(rootPath.resolve(path)));
                            baseMsg.setLine(Integer.valueOf(line));
                            baseMsg.setColumn(Integer.valueOf(column));
                        } else {
                            final BuildMessage msg = new BuildMessage();
                            msg.setLevel(baseMsg.getLevel());
                            msg.setPath(baseMsg.getPath());
                            msg.setLine(baseMsg.getLine());
                            msg.setColumn(baseMsg.getColumn());
                            msg.setText(sb.toString());
                            result.add(msg);
                        }
                        currentLine++;
                        message = allValues.get(currentLine).toCharArray();
                        sb = new StringBuilder();
                        i = 0;
                    }
                }
                return new Result(result, currentLine - 1);
            }

            private String getValue(char[] message, int i) {
                int endIndex = endIndex(message, i);
                return new String(Arrays.copyOfRange(message, i, endIndex));
            }

            private int endIndex(char[] message, int i) {
                for (int j = 0; j < (message.length - i); j++) {
                    if (message[i + j] == ',' || message[i + j] == '\n') {
                        return i + j;
                    }
                }
                return message.length;
            }

            private boolean checkEquals(final char[] message,
                                        final int i,
                                        final String s) {
                if (s == null || s.isEmpty()) {
                    return false;
                }
                if (message.length < (i + s.length())) {
                    return false;
                }
                char[] other = s.toCharArray();
                for (int j = 0; j < other.length; j++) {
                    if (message[i + j] != other[j]) {
                        return false;
                    }
                }
                return true;
            }
        });
    }

    public static BuildResults convertIntoBuildResults(final List<String> mavenOutput,
                                                       final Path path,
                                                       final String workingDir) {

        final BuildResults buildResults = new BuildResults();
        if (mavenOutput.size() > 0) {

            final List<BuildMessage> inserted = new LinkedList<>();
            for (int i = 0; i < mavenOutput.size(); i++) {
                String item = mavenOutput.get(i);

                for (final InputProcessor filter : filters) {
                    if (filter.accept(path, workingDir, item)) {
                        InputProcessor.Result result = filter.process(path, workingDir, mavenOutput, i);
                        inserted.addAll(result.getMessages());
                        i = result.getNewIndex();
                        break;
                    }
                }
            }
            buildResults.addAllBuildMessages(new ArrayList<>(inserted));
        }
        return buildResults;
    }
}
