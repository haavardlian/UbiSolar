package com.sintef_energy.ubisolar.resources;

import com.sintef_energy.ubisolar.Device;
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
@Path("{user}/devices/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DevicesResource {
    private final ServerDAO db;

    public DevicesResource(ServerDAO db) {
        this.db = db;
    }

    @PUT
    @Timed
    public Response createDeviceForUser(@PathParam("user") IntParam user, @Valid Device device) {
        int r = db.createDevice(device);

        throw new WebApplicationException(r == 1 ? Response.Status.CREATED : Response.Status.NOT_MODIFIED);
    }

    @GET
    @Timed
    public List<Device> getDevicesForUser(@PathParam("user") IntParam user) {
        List<Device> devices = db.getDevicesForUser(user.get());

        if(devices != null && !devices.isEmpty())
            return devices;
        else
            throw new WebApplicationException(Response.Status.NOT_FOUND);
    }
}
