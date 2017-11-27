package org.kie.workbench.common.services.backend.compiler.impl.utils;

import org.guvnor.common.services.shared.builder.model.BuildMessage;
import org.guvnor.common.services.shared.message.Level;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.java.nio.file.Path;

public abstract class BaseInputProcessor implements InputProcessor {

    protected BuildMessage getBuildMessage(final BuildMessage origin,
                                           final String text) {
        final BuildMessage msg = new BuildMessage();
        msg.setLevel(origin.getLevel());
        msg.setColumn(origin.getColumn());
        msg.setLine(origin.getLine());
        msg.setPath(origin.getPath());
        msg.setText(text);
        return msg;
    }

    protected BuildMessage getBuildMessage(final Path rootPath,
                                           final String workingDir,
                                           final String item) {
        final BuildMessage msg = new BuildMessage();
        final int indexOfEdnFilePath = item.lastIndexOf(":[");
        final String fileName = item.substring(item.indexOf(workingDir) + workingDir.length(), indexOfEdnFilePath);
        final int endLineInfoIndex = item.lastIndexOf("]");
        final LineColumn lineAndColum = getLineAndColumn(item.substring(indexOfEdnFilePath + 2, endLineInfoIndex));
        if (lineAndColum != null) {
            msg.setLine(Integer.parseInt(lineAndColum.getLine()));
            if (lineAndColum.getColumn() != null) {
                msg.setColumn(Integer.parseInt(lineAndColum.getColumn()));
            }
        }
        msg.setText(item.substring(endLineInfoIndex + 1));
        msg.setPath(Paths.convert(rootPath.resolve(fileName)));
        msg.setLevel(Level.ERROR);

        return msg;
    }

    protected LineColumn getLineAndColumn(final String errorLine) {
        if (errorLine.contains(",")) {
            String[] result = errorLine.split(",");
            return new LineColumn(result[0], result[1]);
        } else if (errorLine.contains(":")) {
            String[] result = errorLine.split(":");
            return new LineColumn(result[0], result[1]);
        } else {
        }
        return new LineColumn(errorLine);
    }
}
