package com.sintef_energy.ubisolar.resources;

import com.sintef_energy.ubisolar.ServerDAO;
import com.sintef_energy.ubisolar.structs.Device;
import com.yammer.dropwizard.jersey.params.IntParam;
import com.yammer.dropwizard.jersey.params.LongParam;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Håvard on 23.04.14.
 */
@Path("user/{user}/sync")
@Produces(MediaType.APPLICATION_JSON)
public class SyncResource {
    private final ServerDAO db;

    public SyncResource(ServerDAO db) {
        this.db = db;
    }

    @GET
    @Path("/device/newest")
    public long getLastEditedDeviceTime() {
        long latest = db.getLastEditedDeviceTime();

        return latest;
    }

    @GET
    @Path("/device/{timestamp}")
    public List<Device> getNewDevices(@PathParam("timestamp") LongParam timestamp, @PathParam("user") IntParam userID) {
        List<Device> devices = db.getUpdatedDevices(userID.get(), timestamp.get());
        if(devices != null && !devices.isEmpty())
            return devices;
        else
            throw new WebApplicationException(Response.Status.NO_CONTENT);
    }

    @PUT
    @Path("/device/")
    public Response syncDevices(@Valid ArrayList<Device> devices) {
        Date date = new Date();
        db.createDevices(devices.iterator(), date.getTime()/1000L);

        throw new WebApplicationException(Response.Status.CREATED);
    }
}
