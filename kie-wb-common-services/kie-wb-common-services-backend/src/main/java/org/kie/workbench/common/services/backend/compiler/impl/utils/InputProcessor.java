package org.kie.workbench.common.services.backend.compiler.impl.utils;

import java.util.List;

import org.guvnor.common.services.project.builder.model.BuildMessage;
import org.uberfire.java.nio.file.Path;

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
