package org.kie.workbench.common.screens.contributors.service;

import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.jboss.errai.bus.server.annotations.Remote;

@Remote
public interface ContributorsService {

    void updateContributors(final WorkspaceProject workspaceProject);

    void updateContributors(final OrganizationalUnit activeSpace);
}
