package org.kie.workbench.common.services.backend.compiler.nio.decorators;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.kie.workbench.common.services.backend.compiler.CompilationResponse;
import org.kie.workbench.common.services.backend.compiler.impl.DefaultCompilationResponse;
import org.kie.workbench.common.services.backend.compiler.nio.NIOCompilationRequest;
import org.kie.workbench.common.services.backend.compiler.nio.NIOMavenCompiler;
import org.kie.workbench.common.services.backend.compiler.nio.decorators.kie.NIOKieAfterDecorator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NIOOutputLogAfterDecorator extends NIOCompilerDecorator {

    private static final Logger logger = LoggerFactory.getLogger(NIOKieAfterDecorator.class);
    private NIOMavenCompiler compiler;

    public NIOOutputLogAfterDecorator(NIOMavenCompiler compiler) {
        this.compiler = compiler;
    }

    @Override
    public CompilationResponse compileSync(NIOCompilationRequest req) {

        CompilationResponse res = compiler.compileSync(req);

        if (res.isSuccessful()) {

            return new DefaultCompilationResponse(Boolean.TRUE,
                                                  getOutput(req.getInfo().getPrjPath(),
                                                            req.getKieCliRequest().getLogFile(),
                                                            req.getKieCliRequest().getRequestUUID()));
        } else {
            return new DefaultCompilationResponse(Boolean.FALSE,
                                                  getOutput(req.getInfo().getPrjPath(),
                                                            req.getKieCliRequest().getLogFile(),
                                                            req.getKieCliRequest().getRequestUUID()));
        }
    }

    private Optional<List<String>> getOutput(Path prj,
                                             Optional<String> log,
                                             String uuid) {
        if (log.isPresent()) {
            StringBuilder sb = new StringBuilder(prj.toAbsolutePath().toString().trim()).append("/").append(log.get().trim()).append(".").append(uuid).append(".log");
            return Optional.of(readTmpLog(sb.toString()));
        } else {
            return Optional.empty();
        }
    }

    private List<String> readTmpLog(String logFile) {
        Path logPath = Paths.get(logFile);
        List<String> log = new ArrayList<>();
        if (Files.isReadable(logPath)) {
            try {
                for (String line : Files.readAllLines(logPath,
                                                      Charset.defaultCharset())) {
                    log.add(line);
                }
                return log;
            } catch (IOException ioe) {
                logger.error(ioe.getMessage());
            }
        }
        return Collections.emptyList();
    }
}
