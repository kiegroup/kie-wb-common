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
package org.kie.workbench.common.services.backend.compiler.nio.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.codehaus.plexus.classworlds.ClassWorld;
import org.drools.compiler.kie.builder.impl.FileKieModule;
import org.drools.core.rule.KieModuleMetaInfo;
import org.kie.api.builder.KieModule;
import org.kie.workbench.common.services.backend.compiler.CompilationResponse;
import org.kie.workbench.common.services.backend.compiler.KieClassLoaderProvider;
import org.kie.workbench.common.services.backend.compiler.configuration.Compilers;
import org.kie.workbench.common.services.backend.compiler.configuration.FileSystemImpl;
import org.kie.workbench.common.services.backend.compiler.external339.KieMavenCli;
import org.kie.workbench.common.services.backend.compiler.impl.DefaultCompilationResponse;
import org.kie.workbench.common.services.backend.compiler.impl.ProcessedPoms;
import org.kie.workbench.common.services.backend.compiler.nio.NIOCompilationRequest;
import org.kie.workbench.common.services.backend.compiler.nio.NIOIncrementalCompilerEnabler;
import org.kie.workbench.common.services.backend.compiler.nio.NIOMavenCompiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Run maven with https://maven.apache.org/ref/3.5.0/maven-embedder/xref/index.html
 * to use Takari plugins like a black box
 * <p>
 * <p>
 * MavenCompiler compiler = new DefaultMavenCompiler(Paths.get("<path_to_maven_repo>"));
 * WorkspaceCompilationInfo info = new WorkspaceCompilationInfo(Paths.get("path_to_prj"), URI.create("git://<address></>:<port></>/<repo>"), compiler, cloned);
 * CompilationRequest req = new DefaultCompilationRequest(info, new String[]{MavenArgs.COMPILE});
 * CompilationResponse res = compiler.compileSync(req);
 */
public class NIODefaultMavenCompiler implements NIOMavenCompiler {

    private static final Logger logger = LoggerFactory.getLogger(NIODefaultMavenCompiler.class);

    private KieMavenCli cli;

    private Path mavenRepo;

    private NIOIncrementalCompilerEnabler enabler;

    public NIODefaultMavenCompiler(Path mavenRepo) {
        this.mavenRepo = mavenRepo;
        cli = new KieMavenCli(FileSystemImpl.NIO);
        enabler = new NIODefaultIncrementalCompilerEnabler(Compilers.JAVAC);
    }

    /**
     * Check if the folder exists and if it's writable and readable
     * @param mavenRepo
     * @return
     */
    public static Boolean isValidMavenRepo(Path mavenRepo) {
        if (mavenRepo.getParent() == null) {
            return Boolean.FALSE;// used because Path("") is considered for Files.exists...
        }
        return Files.exists(mavenRepo) && Files.isDirectory(mavenRepo) && Files.isWritable(mavenRepo) && Files.isReadable(mavenRepo);
    }

    /**
     * Perform a "mvn -v" call to check if the maven home is correct
     * @return
     */
    @Override
    public Boolean isValid() {
        return isValidMavenRepo(this.mavenRepo);
    }

    @Override
    public Path getMavenRepo() {
        return mavenRepo;
    }

    @Override
    public CompilationResponse compileSync(NIOCompilationRequest req) {
        if (logger.isDebugEnabled()) {
            logger.debug("KieCompilationRequest:{}",
                         req);
        }

        //Check if the pom are already processed
        if (!req.getInfo().getEnhancedMainPomFile().isPresent()) {
            ProcessedPoms processedPoms = enabler.process(req);
            if (!processedPoms.getResult()) {
                return new DefaultCompilationResponse(Boolean.FALSE,
                                                      Optional.of("Processing poms failed"),
                                                      Optional.empty());
            }
        }

        // set the maven repo used in this compilation
        req.getKieCliRequest().getRequest().setLocalRepositoryPath(mavenRepo.toAbsolutePath().toString());

        /**
         The classworld is now Created in the NioMavenCompiler and in the InternalNioDefaultMaven compielr for this reasons:
         problem: https://stackoverflow.com/questions/22410706/error-when-execute-mavencli-in-the-loop-maven-embedder
         problem:https://stackoverflow.com/questions/40587683/invocation-of-mavencli-fails-within-a-maven-plugin
         solution:https://dev.eclipse.org/mhonarc/lists/sisu-users/msg00063.html
         */
        ClassWorld kieClassWorld = new ClassWorld("plexus.core",
                                                  getClass().getClassLoader());
        int exitCode = cli.doMain(req.getKieCliRequest(),
                                  kieClassWorld);
        if (exitCode == 0) {

            if (req.getInfo().isKiePluginPresent()) {
                return handleKieMavenPlugin(req);
            }
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

    private CompilationResponse handleKieMavenPlugin(NIOCompilationRequest req) {

        KieTuple kieModuleMetaInfoTuple = readKieModuleMetaInfo(req);
        KieTuple kieModuleTuple = readKieModule(req);
        if (kieModuleMetaInfoTuple.getOptionalObject().isPresent() && kieModuleTuple.getOptionalObject().isPresent()) {
            //@TODO load the dependencies from target/classes
            KieClassLoaderProvider provider = new NIOClassLoaderProviderImpl();
            Optional<List<URI>> optionalDeps = provider.getURISFromAllDependencies(req.getInfo().getPrjPath().toAbsolutePath().toString());
            return new DefaultCompilationResponse(Boolean.TRUE,
                                                  (KieModuleMetaInfo) kieModuleMetaInfoTuple.getOptionalObject().get(),
                                                  (KieModule) kieModuleTuple.getOptionalObject().get(),
                                                  getOutput(req.getInfo().getPrjPath(),
                                                            req.getKieCliRequest().getLogFile(),
                                                            req.getKieCliRequest().getRequestUUID()),
                                                  optionalDeps);
        } else {
            StringBuilder sb = new StringBuilder();
            if (kieModuleMetaInfoTuple.getErrorMsg().isPresent()) {
                sb.append(" Error in the kieModuleMetaInfo from the kieMap:").append(kieModuleMetaInfoTuple.getErrorMsg().get());
            }
            if (kieModuleTuple.getErrorMsg().isPresent()) {
                sb.append(" Error in the kieModule:").append(kieModuleTuple.getErrorMsg().get());
            }
            return new DefaultCompilationResponse(Boolean.FALSE,
                                                  Optional.of(sb.toString()),
                                                  getOutput(req.getInfo().getPrjPath(),
                                                            req.getKieCliRequest().getLogFile(),
                                                            req.getKieCliRequest().getRequestUUID()));
        }
    }

    private KieTuple readKieModuleMetaInfo(NIOCompilationRequest req) {
        /** This part is mandatory because the object loaded in the kie maven plugin is
         * loaded in a different classloader and every accessing cause a ClassCastException
         * Standard for the kieMap's keys -> compilationID + dot + classname
         * */
        StringBuilder sb = new StringBuilder(req.getKieCliRequest().getRequestUUID()).append(".").append(KieModuleMetaInfo.class.getName());
        Object o = req.getKieCliRequest().getMap().get(sb.toString());
        if (o != null) {

            KieTuple tuple = readObjectFromADifferentClassloader(o);

            if (tuple.getOptionalObject().isPresent()) {

                return new KieTuple(tuple.getOptionalObject(),
                                    Optional.empty());
            } else {

                return new KieTuple(Optional.empty(),
                                    tuple.getErrorMsg());
            }
        } else {
            return new KieTuple(Optional.empty(),
                                Optional.of("kieModuleMetaInfo not present in the map"));
        }
    }

    private KieTuple readKieModule(NIOCompilationRequest req) {

        /** This part is mandatory because the object loaded in the kie maven plugin is
         * loaded in a different classloader and every accessing cause a ClassCastException
         * Standard for the kieMap's keys -> compilationID + dot + classname
         * */
        StringBuilder sb = new StringBuilder(req.getKieCliRequest().getRequestUUID()).append(".").append(FileKieModule.class.getName());
        Object o = req.getKieCliRequest().getMap().get(sb.toString());

        if (o != null) {
            KieTuple tuple = readObjectFromADifferentClassloader(o);

            if (tuple.getOptionalObject().isPresent()) {

                return new KieTuple(tuple.getOptionalObject(),
                                    Optional.empty());
            } else {

                return new KieTuple(Optional.empty(),
                                    tuple.getErrorMsg());
            }
        } else {

            return new KieTuple(Optional.empty(),
                                Optional.of("kieModule not present in the map"));
        }
    }

    private KieTuple readObjectFromADifferentClassloader(Object o) {

        ObjectInput in = null;
        ObjectOutput out = null;
        ByteArrayInputStream bis = null;
        ByteArrayOutputStream bos = null;

        try {
            bos = new ByteArrayOutputStream();
            out = new ObjectOutputStream(bos);
            out.writeObject(o);
            out.flush();
            byte[] objBytes = bos.toByteArray();
            bis = new ByteArrayInputStream(objBytes);
            in = new ObjectInputStream(bis);
            Object newObj = in.readObject();
            return new KieTuple(Optional.of(newObj),
                                Optional.empty());
        } catch (NotSerializableException nse) {
            nse.printStackTrace();
            StringBuilder sb = new StringBuilder("NotSerializableException:").append(nse.getMessage());
            return new KieTuple(Optional.empty(),
                                Optional.of(sb.toString()));
        } catch (IOException ioe) {
            StringBuilder sb = new StringBuilder("IOException:").append(ioe.getMessage());
            return new KieTuple(Optional.empty(),
                                Optional.of(sb.toString()));
        } catch (ClassNotFoundException cnfe) {
            StringBuilder sb = new StringBuilder("ClassNotFoundException:").append(cnfe.getMessage());
            return new KieTuple(Optional.empty(),
                                Optional.of(sb.toString()));
        } catch (Exception e) {
            StringBuilder sb = new StringBuilder("Exception:").append(e.getMessage());
            return new KieTuple(Optional.empty(),
                                Optional.of(sb.toString()));
        } finally {
            try {
                if(bos != null) {
                    bos.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                logger.error(ex.getMessage());
            }
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

    static class KieTuple {

        private Optional<Object> optionalObj;
        private Optional<String> errorMsg;

        public KieTuple(Optional<Object> optionalObj,
                        Optional<String> errorMsg) {
            this.optionalObj = optionalObj;
            this.errorMsg = errorMsg;
        }

        public Optional<Object> getOptionalObject() {
            return optionalObj;
        }

        public Optional<String> getErrorMsg() {
            return errorMsg;
        }
    }
}