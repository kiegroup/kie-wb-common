package org.kie.workbench.common.services.shared.healthcheck;

import javax.annotation.security.PermitAll;
import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.MediaType.TEXT_PLAIN;
import static javax.ws.rs.core.Response.Status.SERVICE_UNAVAILABLE;

@Path("/")
@ApplicationScoped
public class HealthCheckService {

    private boolean ready = true;

    @GET
    @PermitAll
    @Produces(TEXT_PLAIN)
    @Path("/ready")
    public Response isReady() {
        return getHealthCheckResponse();
    }

    @GET
    @PermitAll
    @Produces(TEXT_PLAIN)
    @Path("/healthy")
    public Response isHealthy() {
        return getHealthCheckResponse();
    }

    private Response getHealthCheckResponse() {
        return ready
                ? Response.ok(true).build()
                : Response.status(SERVICE_UNAVAILABLE).entity(false).build();
    }
}
