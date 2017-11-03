package org.kie.workbench.common.services.backend.compiler.impl.utils;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.ContextNotActiveException;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.guvnor.common.services.backend.cache.BuilderCache;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.m2repo.backend.server.GuvnorM2Repository;
import org.guvnor.m2repo.backend.server.repositories.ArtifactRepositoryService;
import org.jboss.errai.security.shared.api.identity.User;
import org.kie.workbench.common.services.backend.builder.af.KieAFBuilder;
import org.kie.workbench.common.services.backend.builder.af.impl.DefaultKieAFBuilder;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;
import static org.kie.workbench.common.services.backend.compiler.impl.utils.MavenUtils.getMavenRepoDir;
import static org.uberfire.backend.server.util.Paths.convert;

@ApplicationScoped
public class BuilderUtils {

    private static final String SYSTEM_USER = "system";

    @Inject
    private GuvnorM2Repository guvnorM2Repository;

    @Inject
    private BuilderCache<Project, KieAFBuilder> builderCache;

    @Inject
    private Instance<User> identity;

    public KieAFBuilder getBuilder(final Project project) {
        KieAFBuilder builder = builderCache.getEntry(checkNotNull("project", project));
        if (builder == null) {
            builder = new DefaultKieAFBuilder(convert(project.getRootPath()),
                                              getMavenRepoDir(guvnorM2Repository.getM2RepositoryDir(ArtifactRepositoryService.GLOBAL_M2_REPO_NAME)),
                                              getActiveUser());

            builderCache.setEntry(project, builder);
        }
        return builder;
    }

    private String getActiveUser() {
        if (identity.isUnsatisfied()) {
            return SYSTEM_USER;
        }
        try {
            return identity.get().getIdentifier();
        } catch (ContextNotActiveException e) {
            return SYSTEM_USER;
        }
    }
}
