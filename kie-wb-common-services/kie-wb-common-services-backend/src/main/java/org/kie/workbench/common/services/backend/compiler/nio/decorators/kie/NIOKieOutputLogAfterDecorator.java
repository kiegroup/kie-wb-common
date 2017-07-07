package org.kie.workbench.common.services.backend.compiler.nio.decorators.kie;

import org.kie.workbench.common.services.backend.compiler.KieCompilationResponse;
import org.kie.workbench.common.services.backend.compiler.impl.DefaultKieCompilationResponse;
import org.kie.workbench.common.services.backend.compiler.impl.LogUtils;
import org.kie.workbench.common.services.backend.compiler.nio.NIOCompilationRequest;
import org.kie.workbench.common.services.backend.compiler.nio.NIOKieMavenCompiler;

/***
 * After decorator to read and store the maven output into a List<String> in the CompilationResponse with NIO2 on a Kie Project
 */
public class NIOKieOutputLogAfterDecorator implements NIOKieCompilerDecorator {

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
