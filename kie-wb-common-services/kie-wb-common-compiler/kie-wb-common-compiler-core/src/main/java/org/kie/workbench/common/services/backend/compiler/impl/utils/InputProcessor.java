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
package org.kie.workbench.common.services.backend.compiler.impl.utils;

import java.util.List;

import org.guvnor.common.services.project.builder.model.BuildMessage;
import org.uberfire.java.nio.file.Path;

/***
 * Used in the conversion between Maven Output and other objects used into different representation
 */
public interface InputProcessor {

    boolean accept(final Path rootPath,
                   final String workingDir,
                   final String value);

    Result process(final Path rootPath,
                   final String workingDir,
                   final List<String> allValues,
                   final int index);

    class Result {

        private final List<BuildMessage> messages;
        private final int newIndex;

        public Result(List<BuildMessage> messages, int newIndex) {
            this.messages = messages;
            this.newIndex = newIndex;
        }

        public List<BuildMessage> getMessages() {
            return messages;
        }

        public int getNewIndex() {
            return newIndex;
        }
    }

    class LineColumn {

        private final String line;
        private final String column;

        public LineColumn(String line) {
            this(line, null);
        }

        public LineColumn(String line, String column) {
            this.line = line;
            this.column = column;
        }

        public String getLine() {
            return line;
        }

        public String getColumn() {
            return column;
        }
    }
}
