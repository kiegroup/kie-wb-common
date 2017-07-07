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
package org.kie.workbench.common.services.backend.compiler.internalNIO.decorators.kie;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.drools.compiler.kie.builder.impl.FileKieModule;
import org.drools.core.rule.KieModuleMetaInfo;
import org.kie.api.builder.KieModule;
import org.kie.workbench.common.services.backend.compiler.CompilationResponse;
import org.kie.workbench.common.services.backend.compiler.AFClassLoaderProvider;
import org.kie.workbench.common.services.backend.compiler.KieCompilationResponse;
import org.kie.workbench.common.services.backend.compiler.impl.DefaultKieCompilationResponse;
import org.kie.workbench.common.services.backend.compiler.internalNIO.InternalNIOCompilationRequest;
import org.kie.workbench.common.services.backend.compiler.internalNIO.InternalNIOKieMavenCompiler;
import org.kie.workbench.common.services.backend.compiler.internalNIO.impl.InternalNIOClassLoaderProviderImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InternalNIOKieAfterDecorator extends InternalNIOKieCompilerDecorator implements InternalNIOKieMavenCompiler {

    private static final Logger logger = LoggerFactory.getLogger(InternalNIOKieAfterDecorator.class);
    private InternalNIOKieMavenCompiler compiler;

    public InternalNIOKieAfterDecorator(InternalNIOKieMavenCompiler compiler) {
        this.compiler = compiler;
    }

    @Override
    public KieCompilationResponse compileSync(InternalNIOCompilationRequest req) {
        KieCompilationResponse res = compiler.compileSync(req);
        if (res.isSuccessful()) {

            if (req.getInfo().isKiePluginPresent()) {
                return handleKieMavenPlugin(req,
                                            res);
            }
        }
        return res;
    }

    private KieCompilationResponse handleKieMavenPlugin(InternalNIOCompilationRequest req,
                                                        CompilationResponse res) {

        InternalNIOKieAfterDecorator.KieTuple kieModuleMetaInfoTuple = readKieModuleMetaInfo(req);
        InternalNIOKieAfterDecorator.KieTuple kieModuleTuple = readKieModule(req);
        if (kieModuleMetaInfoTuple.getOptionalObject().isPresent() && kieModuleTuple.getOptionalObject().isPresent()) {

            AFClassLoaderProvider provider = new InternalNIOClassLoaderProviderImpl();
            Optional<List<URI>> optionalDeps = provider.getURISFromAllDependencies(req.getInfo().getPrjPath().toAbsolutePath().toString());
            return new DefaultKieCompilationResponse(Boolean.TRUE,
                                                     (KieModuleMetaInfo) kieModuleMetaInfoTuple.getOptionalObject().get(),
                                                     (KieModule) kieModuleTuple.getOptionalObject().get(),
                                                     res.getMavenOutput(),
                                                     optionalDeps);
        } else {
            StringBuilder sb = new StringBuilder();
            if (kieModuleMetaInfoTuple.getErrorMsg().isPresent()) {
                sb.append(" Error in the kieModuleMetaInfo from the kieMap:").append(kieModuleMetaInfoTuple.getErrorMsg().get());
            }
            if (kieModuleTuple.getErrorMsg().isPresent()) {
                sb.append(" Error in the kieModule:").append(kieModuleTuple.getErrorMsg().get());
            }
            return new DefaultKieCompilationResponse(Boolean.FALSE,
                                                     Optional.of(sb.toString()),
                                                     res.getMavenOutput());
        }
    }

    private InternalNIOKieAfterDecorator.KieTuple readKieModuleMetaInfo(InternalNIOCompilationRequest req) {
        /** This part is mandatory because the object loaded in the kie maven plugin is
         * loaded in a different classloader and every accessing cause a ClassCastException
         * Standard for the kieMap's keys -> compilationID + dot + classname
         * */
        StringBuilder sb = new StringBuilder(req.getKieCliRequest().getRequestUUID()).append(".").append(KieModuleMetaInfo.class.getName());
        Object o = req.getKieCliRequest().getMap().get(sb.toString());
        if (o != null) {
            InternalNIOKieAfterDecorator.KieTuple tuple = readObjectFromADifferentClassloader(o);

            if (tuple.getOptionalObject().isPresent()) {

                return new InternalNIOKieAfterDecorator.KieTuple(tuple.getOptionalObject(),
                                                                 Optional.empty());
            } else {

                return new InternalNIOKieAfterDecorator.KieTuple(Optional.empty(),
                                                                 tuple.getErrorMsg());
            }
        } else {
            return new InternalNIOKieAfterDecorator.KieTuple(Optional.empty(),
                                                             Optional.of("kieModuleMetaInfo not present in the map"));
        }
    }

    private InternalNIOKieAfterDecorator.KieTuple readKieModule(InternalNIOCompilationRequest req) {
        /** This part is mandatory because the object loaded in the kie maven plugin is
         * loaded in a different classloader and every accessing cause a ClassCastException
         * Standard for the kieMap's keys -> compilationID + dot + classname
         * */
        StringBuilder sb = new StringBuilder(req.getKieCliRequest().getRequestUUID()).append(".").append(FileKieModule.class.getName());
        Object o = req.getKieCliRequest().getMap().get(sb.toString());

        if (o != null) {
            InternalNIOKieAfterDecorator.KieTuple tuple = readObjectFromADifferentClassloader(o);

            if (tuple.getOptionalObject().isPresent()) {

                return new InternalNIOKieAfterDecorator.KieTuple(tuple.getOptionalObject(),
                                                                 Optional.empty());
            } else {

                return new InternalNIOKieAfterDecorator.KieTuple(Optional.empty(),
                                                                 tuple.getErrorMsg());
            }
        } else {

            return new InternalNIOKieAfterDecorator.KieTuple(Optional.empty(),
                                                             Optional.of("kieModule not present in the map"));
        }
    }

    private InternalNIOKieAfterDecorator.KieTuple readObjectFromADifferentClassloader(Object o) {

        ObjectInput in = null;
        ObjectOutput out;
        ByteArrayInputStream bis;
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
            return new InternalNIOKieAfterDecorator.KieTuple(Optional.of(newObj),
                                                             Optional.empty());
        } catch (NotSerializableException nse) {
            nse.printStackTrace();
            StringBuilder sb = new StringBuilder("NotSerializableException:").append(nse.getMessage());
            return new InternalNIOKieAfterDecorator.KieTuple(Optional.empty(),
                                                             Optional.of(sb.toString()));
        } catch (IOException ioe) {
            StringBuilder sb = new StringBuilder("IOException:").append(ioe.getMessage());
            return new InternalNIOKieAfterDecorator.KieTuple(Optional.empty(),
                                                             Optional.of(sb.toString()));
        } catch (ClassNotFoundException cnfe) {
            StringBuilder sb = new StringBuilder("ClassNotFoundException:").append(cnfe.getMessage());
            return new InternalNIOKieAfterDecorator.KieTuple(Optional.empty(),
                                                             Optional.of(sb.toString()));
        } catch (Exception e) {
            StringBuilder sb = new StringBuilder("Exception:").append(e.getMessage());
            return new InternalNIOKieAfterDecorator.KieTuple(Optional.empty(),
                                                             Optional.of(sb.toString()));
        } finally {
            try {
                if (bos != null) {
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
