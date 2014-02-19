package com.sintef_energy.ubisolar;

import com.yammer.dropwizard.jersey.params.IntParam;
import com.yammer.metrics.annotation.Timed;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by haavard on 2/19/14.
 */
@Path("{user}/devices/{id}")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DeviceResource {
    private final ServerDAO db;

    public DeviceResource(ServerDAO db) {
        this.db = db;
    }

    @GET
    public Device getDeviceById(@PathParam("user") IntParam user, @PathParam("id") IntParam id) {
        Device d = db.getDeviceForUserById(user.get(), id.get());

        if(d != null) return d;
        else throw new WebApplicationException(Response.Status.NOT_FOUND);
    }
}