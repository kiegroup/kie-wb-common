package org.kie.workbench.common.screens.impl;

import java.util.Collection;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.kie.workbench.common.screens.library.api.SpacesScreenService;
import org.kie.workbench.common.screens.library.api.preferences.LibraryInternalPreferencesPortableGeneratedImpl;
import org.uberfire.preferences.shared.bean.PreferenceBeanServerStore;

public class SpacesScreenServiceImpl implements SpacesScreenService {

    @Inject
    public OrganizationalUnitService organizationalUnitService;

    @Inject
    public PreferenceBeanServerStore preferenceBeanServerStore;

    @Override
    public Collection<OrganizationalUnit> getSpaces() {
        return organizationalUnitService.getOrganizationalUnits();
    }

    @Override
    public Response savePreference(final LibraryInternalPreferencesPortableGeneratedImpl preference) {
        preferenceBeanServerStore.save(preference);
        return Response.ok().build();
    }

    @Override
    public OrganizationalUnit getSpace(final String name) {
        return organizationalUnitService.getOrganizationalUnit(name);
    }

    @Override
    public boolean isValidGroupId(final String groupId) {
        return organizationalUnitService.isValidGroupId(groupId);
    }

    @Override
    public Response postSpace(final NewSpace newSpace) {
        organizationalUnitService.createOrganizationalUnit(newSpace.name, newSpace.groupId);
        return Response.status(201).build();
    }
}
