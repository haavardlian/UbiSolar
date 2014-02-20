package com.sintef_energy.ubisolar.resources;

import com.sintef_energy.ubisolar.DeviceUsage;
import com.sintef_energy.ubisolar.ServerDAO;
import com.yammer.dropwizard.jersey.params.IntParam;
import com.yammer.metrics.annotation.Timed;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created by haavard on 2/19/14.
 */
@Path("{user}/usage/{device}")
@Produces(MediaType.APPLICATION_JSON)
public class DeviceUsageResource {
    private final ServerDAO db;

    public DeviceUsageResource(ServerDAO db) {
        this.db = db;
    }

    @GET
    @Timed
    public List<DeviceUsage> getUsageForDevice(@PathParam("device") IntParam device) {
        List<DeviceUsage> usage = db.getUsageForDevice(device.get());
        if(usage != null && !usage.isEmpty())
            return usage;
        else
            throw new WebApplicationException(Response.Status.NOT_FOUND);
    }

    @PUT
    @Timed
    public Response addUsageForDevice(@PathParam("user") IntParam user, @PathParam("device") IntParam device,
                                      @Valid DeviceUsage usage) {
        int r;
        if(device.get() == usage.getDeviceId())
             r = db.addUsageForDevice(usage);
        else r = 0;

        throw new WebApplicationException(r == 1 ? Response.Status.CREATED : Response.Status.NOT_MODIFIED);
    }
}
