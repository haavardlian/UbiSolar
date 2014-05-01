package com.sintef_energy.ubisolar.resources;

import com.sintef_energy.ubisolar.ServerDAO;
import com.sintef_energy.ubisolar.structs.Device;
import com.sintef_energy.ubisolar.structs.DeviceUsage;
import com.yammer.dropwizard.jersey.params.LongParam;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Håvard on 01.05.14.
 */
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("user/{user}/")
public class DataGeneratorResource {
    private ServerDAO db;
    private ArrayList<Device> devices;
    long n = 125363;
    public DataGeneratorResource(ServerDAO db) {
        this.db = db;
        this.devices = new ArrayList<Device>();


    }

    private void generateDevices(ArrayList<Device> devices, long user) {
        long time = System.currentTimeMillis();
        devices.add(new Device(time+1, user, "TV", "This is your TV", time/1000l, false, 1));
        devices.add(new Device(time+2, user, "Stove", "This is your Stove", time/1000l, false, 2));
        devices.add(new Device(time+3, user, "Fridge", "This is your Fridge", time/1000l, false, 3));
        devices.add(new Device(time+4, user, "Heater", "This is your Heater", time/1000l, false, 4));

    }

    private ArrayList<DeviceUsage> generateUsage(Device d) {
        ArrayList<DeviceUsage> usage = new ArrayList<DeviceUsage>();
        Random r = new Random();
        long time = System.currentTimeMillis();
        double rangeMin = 5.0, rangeMax = 20.0;
        double random;

        for(int i = 0; i < 1000; i++) {
            random = rangeMin + (rangeMax - rangeMin) * r.nextDouble();
            rangeMin = random - 5;
            rangeMax = random + 5;
            if(random < 0) random = -random;
            usage.add(new DeviceUsage(n++, d.getId(), (time/1000L) - (i*3600), random, false, time));
        }

        return usage;
    }

    @GET
    @Path("generate/")
    public Response generateData(@PathParam("user") LongParam user) {
        ArrayList<DeviceUsage> usage;
        this.generateDevices(this.devices, user.get());
        db.createDevices(this.devices.iterator());

        for(Device d : this.devices) {
            usage = generateUsage(d);

            db.addUsageForDevices(usage.iterator());
        }


        return Response.status(Response.Status.OK).build();
    }



}
