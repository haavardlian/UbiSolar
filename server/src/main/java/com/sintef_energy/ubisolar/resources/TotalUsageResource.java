package com.sintef_energy.ubisolar.resources;

/**
 * Created by thb on 12.02.14.
 */
import com.sintef_energy.ubisolar.ServerDAO;
import com.sintef_energy.ubisolar.TotalUsage;
import com.yammer.dropwizard.jersey.params.IntParam;
import com.yammer.metrics.annotation.Timed;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("{user}/usage/total/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TotalUsageResource {
    private final ServerDAO db;

    public TotalUsageResource(ServerDAO db) {
        this.db = db;
    }

    @GET
    @Timed
    public List<TotalUsage> getPowerUsage(@PathParam("user") IntParam user) {
        List<TotalUsage> usage = db.getTotalUsageForUser(user.get());
        if(usage != null && !usage.isEmpty())
            return usage;
        else
            throw new WebApplicationException(Response.Status.NOT_FOUND);
    }

    @PUT
    @Timed
    public Response addPowerUsage(@PathParam("user") IntParam user, @Valid TotalUsage usage) {
        int r = db.addTotalUsageForUser(usage);

        if(r == 1) throw new WebApplicationException(Response.Status.CREATED);
        else throw new WebApplicationException(Response.Status.NOT_MODIFIED);
    }
}