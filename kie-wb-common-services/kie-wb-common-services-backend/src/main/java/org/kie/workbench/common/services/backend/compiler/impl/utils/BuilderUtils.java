package org.kie.workbench.common.services.backend.compiler.impl.utils;

import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.guvnor.m2repo.backend.server.GuvnorM2Repository;
import org.jboss.errai.security.shared.api.identity.User;
import org.kie.workbench.common.services.backend.builder.af.KieAFBuilder;
import org.guvnor.common.services.backend.cache.BuilderCache;
import org.guvnor.common.services.backend.cache.GitCache;
import org.uberfire.java.nio.file.Path;

@ApplicationScoped
public class BuilderUtils {

    @Inject
    private GuvnorM2Repository guvnorM2Repository;
    @Inject
    private Instance<User> identity;
    @Inject
    private GitCache gitCache;
    @Inject
    private BuilderCache builderCache;

    public Optional<KieAFBuilder> getBuilder(String uri, Path nioPath) {
        KieAFBuilder builder = KieAFBuilderUtil.getKieAFBuilder(uri, nioPath, gitCache, builderCache, guvnorM2Repository, KieAFBuilderUtil.getIdentifier(identity));
        return Optional.ofNullable(builder);
    }
}
