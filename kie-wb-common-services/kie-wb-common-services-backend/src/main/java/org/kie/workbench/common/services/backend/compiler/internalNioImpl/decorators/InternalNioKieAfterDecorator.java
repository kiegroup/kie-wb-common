package org.kie.workbench.common.services.backend.compiler.internalNioImpl.decorators;

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
import org.kie.workbench.common.services.backend.compiler.KieClassLoaderProvider;
import org.kie.workbench.common.services.backend.compiler.KieCompilationResponse;
import org.kie.workbench.common.services.backend.compiler.impl.DefaultCompilationResponse;
import org.kie.workbench.common.services.backend.compiler.impl.DefaultKieCompilationResponse;
import org.kie.workbench.common.services.backend.compiler.internalNioImpl.InternalNioImplCompilationRequest;
import org.kie.workbench.common.services.backend.compiler.internalNioImpl.InternalNioImplMavenCompiler;
import org.kie.workbench.common.services.backend.compiler.internalNioImpl.impl.InternalNioImplClassLoaderProviderImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InternalNioKieAfterDecorator extends InternalNioImplCompilerDecorator {

    private static final Logger logger = LoggerFactory.getLogger(InternalNioKieAfterDecorator.class);
    private InternalNioImplMavenCompiler compiler;

    public InternalNioKieAfterDecorator(InternalNioImplMavenCompiler compiler) {
        this.compiler = compiler;
    }

    @Override
    public CompilationResponse compileSync(InternalNioImplCompilationRequest req) {
        CompilationResponse res = compiler.compileSync(req);
        if (res.isSuccessful()) {

            if (req.getInfo().isKiePluginPresent()) {
                return handleKieMavenPlugin(req, res);
            }

        }
        return res;
    }

    private KieCompilationResponse handleKieMavenPlugin(InternalNioImplCompilationRequest req, CompilationResponse res) {

        InternalNioKieAfterDecorator.KieTuple kieModuleMetaInfoTuple = readKieModuleMetaInfo(req);
        InternalNioKieAfterDecorator.KieTuple kieModuleTuple = readKieModule(req);
        if (kieModuleMetaInfoTuple.getOptionalObject().isPresent() && kieModuleTuple.getOptionalObject().isPresent()) {

            KieClassLoaderProvider provider = new InternalNioImplClassLoaderProviderImpl();
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

    private InternalNioKieAfterDecorator.KieTuple readKieModuleMetaInfo(InternalNioImplCompilationRequest req) {
        /** This part is mandatory because the object loaded in the kie maven plugin is
         * loaded in a different classloader and every accessing cause a ClassCastException
         * Standard for the kieMap's keys -> compilationID + dot + classname
         * */
        StringBuilder sb = new StringBuilder(req.getKieCliRequest().getRequestUUID()).append(".").append(KieModuleMetaInfo.class.getName());
        Object o = req.getKieCliRequest().getMap().get(sb.toString());
        if (o != null) {
            InternalNioKieAfterDecorator.KieTuple tuple = readObjectFromADifferentClassloader(o);

            if (tuple.getOptionalObject().isPresent()) {

                return new InternalNioKieAfterDecorator.KieTuple(tuple.getOptionalObject(),
                                                                 Optional.empty());
            } else {

                return new InternalNioKieAfterDecorator.KieTuple(Optional.empty(),
                                                                 tuple.getErrorMsg());
            }
        } else {
            return new InternalNioKieAfterDecorator.KieTuple(Optional.empty(),
                                                             Optional.of("kieModuleMetaInfo not present in the map"));
        }
    }

    private InternalNioKieAfterDecorator.KieTuple readKieModule(InternalNioImplCompilationRequest req) {
        /** This part is mandatory because the object loaded in the kie maven plugin is
         * loaded in a different classloader and every accessing cause a ClassCastException
         * Standard for the kieMap's keys -> compilationID + dot + classname
         * */
        StringBuilder sb = new StringBuilder(req.getKieCliRequest().getRequestUUID()).append(".").append(FileKieModule.class.getName());
        Object o = req.getKieCliRequest().getMap().get(sb.toString());

        if (o != null) {
            InternalNioKieAfterDecorator.KieTuple tuple = readObjectFromADifferentClassloader(o);

            if (tuple.getOptionalObject().isPresent()) {

                return new InternalNioKieAfterDecorator.KieTuple(tuple.getOptionalObject(),
                                                                 Optional.empty());
            } else {

                return new InternalNioKieAfterDecorator.KieTuple(Optional.empty(),
                                                                 tuple.getErrorMsg());
            }
        } else {

            return new InternalNioKieAfterDecorator.KieTuple(Optional.empty(),
                                                             Optional.of("kieModule not present in the map"));
        }
    }

    private InternalNioKieAfterDecorator.KieTuple readObjectFromADifferentClassloader(Object o) {

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
            return new InternalNioKieAfterDecorator.KieTuple(Optional.of(newObj),
                                                             Optional.empty());
        } catch (NotSerializableException nse) {
            nse.printStackTrace();
            StringBuilder sb = new StringBuilder("NotSerializableException:").append(nse.getMessage());
            return new InternalNioKieAfterDecorator.KieTuple(Optional.empty(),
                                                             Optional.of(sb.toString()));
        } catch (IOException ioe) {
            StringBuilder sb = new StringBuilder("IOException:").append(ioe.getMessage());
            return new InternalNioKieAfterDecorator.KieTuple(Optional.empty(),
                                                             Optional.of(sb.toString()));
        } catch (ClassNotFoundException cnfe) {
            StringBuilder sb = new StringBuilder("ClassNotFoundException:").append(cnfe.getMessage());
            return new InternalNioKieAfterDecorator.KieTuple(Optional.empty(),
                                                             Optional.of(sb.toString()));
        } catch (Exception e) {
            StringBuilder sb = new StringBuilder("Exception:").append(e.getMessage());
            return new InternalNioKieAfterDecorator.KieTuple(Optional.empty(),
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
