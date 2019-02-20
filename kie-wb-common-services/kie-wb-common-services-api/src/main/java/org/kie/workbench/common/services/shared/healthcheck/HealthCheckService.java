/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

    boolean ready = true;

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
