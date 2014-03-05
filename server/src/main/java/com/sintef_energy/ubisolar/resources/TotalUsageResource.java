package com.sintef_energy.ubisolar.resources;

/**
 * Created by thb on 12.02.14.
 */
import com.sintef_energy.ubisolar.ServerDAO;
import com.sintef_energy.ubisolar.structs.SimpleJSONMessage;
import com.sintef_energy.ubisolar.structs.TotalUsage;
import com.yammer.dropwizard.jersey.params.IntParam;
import com.yammer.metrics.annotation.Timed;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("user/{user}/usage/total/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TotalUsageResource {
    private final ServerDAO db;

    public TotalUsageResource(ServerDAO db) {
        this.db = db;
    }



    @GET
    @Path("{interval}/")
    @Timed
    public List<TotalUsage> getPowerUsage(@PathParam("user") IntParam user, @PathParam("interval") String interval) {
        List<TotalUsage> usage = db.getTotalUsageForUser(user.get());
        if(usage != null && !usage.isEmpty()) return usage;
        else throw new WebApplicationException(Response.Status.NOT_FOUND);
    }

    @PUT
    @Timed
    public Response addPowerUsage(@PathParam("user") IntParam user, @Valid TotalUsage usage) {
        int result = db.addTotalUsageForUser(usage);

        if(result == 1) return  Response.status(Response.Status.CREATED).entity(new SimpleJSONMessage("Usage added")).build();
        else throw new WebApplicationException(Response.Status.NOT_MODIFIED);
    }
}