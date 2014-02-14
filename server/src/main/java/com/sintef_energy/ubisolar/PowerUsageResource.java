package com.sintef_energy.ubisolar;

/**
 * Created by thb on 12.02.14.
 */
import com.yammer.metrics.annotation.Timed;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("{user}/powerUsage")
@Produces(MediaType.APPLICATION_JSON)
class PowerUsageResource {
    private final ServerDAO db;

    public PowerUsageResource(ServerDAO db) {
        this.db = db;
    }

    @GET
    @Timed
    public PowerUsageResponse getPowerUsage(@PathParam("user") String user) {
        throw new WebApplicationException(Response.Status.NOT_ACCEPTABLE);
    }

    @POST
    @Timed
    public PowerUsageResponse updatePowerUsage(@PathParam("userID") String userID) {
        throw new WebApplicationException(Response.Status.NOT_MODIFIED);
    }
}

@Path("{user}/powerUsageForDevice/{device}")
@Produces(MediaType.APPLICATION_JSON)
class PowerUsageDeviceResource {

}