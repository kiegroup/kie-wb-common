package org.kie.workbench.common.services.backend.compiler.nio.decorators.kie;

import org.kie.workbench.common.services.backend.compiler.KieCompilationResponse;
import org.kie.workbench.common.services.backend.compiler.impl.DefaultKieCompilationResponse;
import org.kie.workbench.common.services.backend.compiler.impl.LogUtils;
import org.kie.workbench.common.services.backend.compiler.nio.NIOCompilationRequest;
import org.kie.workbench.common.services.backend.compiler.nio.NIOKieMavenCompiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NIOKieOutputLogAfterDecorator extends NIOKieCompilerDecorator {

    private static final Logger logger = LoggerFactory.getLogger(NIOKieAfterDecorator.class);
    private NIOKieMavenCompiler compiler;

    public NIOKieOutputLogAfterDecorator(NIOKieMavenCompiler compiler) {
        this.compiler = compiler;
    }

    @Override
    public KieCompilationResponse compileSync(NIOCompilationRequest req) {

        KieCompilationResponse res = compiler.compileSync(req);

        if (res.isSuccessful()) {

            return new DefaultKieCompilationResponse(Boolean.TRUE,
                                                     LogUtils.getOutput(req.getInfo().getPrjPath().toAbsolutePath().toString(),
                                                                        req.getKieCliRequest().getLogFile(),
                                                                        req.getKieCliRequest().getRequestUUID()));
        } else {
            return new DefaultKieCompilationResponse(Boolean.FALSE,
                                                     LogUtils.getOutput(req.getInfo().getPrjPath().toAbsolutePath().toString(),
                                                                        req.getKieCliRequest().getLogFile(),
                                                                        req.getKieCliRequest().getRequestUUID()));
        }
    }
}
