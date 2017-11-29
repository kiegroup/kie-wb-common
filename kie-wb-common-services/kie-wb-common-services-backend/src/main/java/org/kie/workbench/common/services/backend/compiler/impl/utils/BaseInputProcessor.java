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

}
