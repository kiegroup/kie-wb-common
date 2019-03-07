package org.kie.workbench.common.profile.api.preferences;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.jboss.errai.bus.server.annotations.Remote;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Remote
@Path("/homeScreen")
@Produces(APPLICATION_JSON)
public interface HomeScreenService {

    @GET
    @Path("/profilePreference")
    Profile profilePreference();
}
