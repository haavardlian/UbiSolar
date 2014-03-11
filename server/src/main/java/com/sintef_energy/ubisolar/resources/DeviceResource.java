package com.sintef_energy.ubisolar.resources;

import com.sintef_energy.ubisolar.structs.Device;
import com.sintef_energy.ubisolar.ServerDAO;
import com.sintef_energy.ubisolar.structs.SimpleJSONMessage;
import com.yammer.dropwizard.jersey.params.IntParam;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by haavard on 2/19/14.
 */
@Path("user/{user}/devices/{id}")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DeviceResource {
    private final ServerDAO db;

    public DeviceResource(ServerDAO db) {
        this.db = db;
    }

    @DELETE
    public Response deleteDeviceById(@PathParam("user") IntParam user, @PathParam("id") IntParam id) {
        int result = db.deleteDeviceForUserById(user.get(), id.get());

        if(result == 1) return Response.ok(new SimpleJSONMessage("Device deleted")).build();
        else throw new WebApplicationException(Response.Status.NOT_MODIFIED);
    }

    @GET
    public Device getDeviceById(@PathParam("user") IntParam user, @PathParam("id") IntParam id) {
        Device device = db.getDeviceForUserById(user.get(), id.get());
        if(device != null) return device;
        else throw new WebApplicationException(Response.Status.NOT_FOUND);
    }
}