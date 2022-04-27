package org.kie.workbench.common.screens.contributors.backend;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.workbench.common.screens.contributors.backend.dataset.ContributorsManager;
import org.kie.workbench.common.screens.contributors.service.ContributorsService;

@Service
@ApplicationScoped
public class ContributorsServiceImpl
        implements ContributorsService {

    @Inject
    ContributorsManager contributorsManager;

    @Override
    public void updateContributors(final WorkspaceProject workspaceProject) {
        contributorsManager.onUpdate(workspaceProject);
    }

    @Override
    public void updateContributors(final OrganizationalUnit organizationalUnit) {
        contributorsManager.onUpdate(organizationalUnit);
    }
}
