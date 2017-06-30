package org.kie.workbench.common.services.backend.compiler.nio.impl;

import org.kie.workbench.common.services.backend.compiler.KieCompilationResponse;
import org.kie.workbench.common.services.backend.compiler.nio.NIOCompilationRequest;
import org.kie.workbench.common.services.backend.compiler.nio.NioKieMavenCompiler;

public class NioKieDefaultMavenCompiler extends NIODefaultMavenCompiler implements NioKieMavenCompiler {

    public NioKieDefaultMavenCompiler(){
        super();
    }

    @Override
    public KieCompilationResponse compileSync(NIOCompilationRequest req) {
        return (KieCompilationResponse) super.compileSync(req);
    }
}
