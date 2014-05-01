package com.sintef_energy.ubisolar.resources;

import com.sintef_energy.ubisolar.ServerDAO;
import com.sintef_energy.ubisolar.structs.Device;
import com.sintef_energy.ubisolar.structs.DeviceUsage;
import com.sun.prism.Texture;
import com.yammer.dropwizard.jersey.params.LongParam;

import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Date;
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

    public DataGeneratorResource(ServerDAO db) {
        this.db = db;
        this.devices = new ArrayList<Device>();


    }

    private void generateDevices(ArrayList<Device> devices, long user) {
        long time = System.currentTimeMillis() / 1000l;
        devices.add(new Device(System.currentTimeMillis(), user, "TV", "This is your TV", time, false));
        devices.add(new Device(System.currentTimeMillis(), user, "Stove", "This is your Stove", time, false));
        devices.add(new Device(System.currentTimeMillis(), user, "Fridge", "This is your Fridge", time, false));
        devices.add(new Device(System.currentTimeMillis(), user, "Heater", "This is your Heater", time, false));

    }

    private ArrayList<DeviceUsage> generateUsage(Device d) {
        ArrayList<DeviceUsage> usage = new ArrayList<DeviceUsage>();
        Random r = new Random();
        long time = System.currentTimeMillis() / 1000L;
        double rangeMin = 50.0, rangeMax = 400.0;
        double random;

        for(int i = 0; i < 200; i++) {
            random = rangeMin + (rangeMax - rangeMin) * r.nextDouble();
            rangeMin = random - 50;
            rangeMax = random + 50;
            usage.add(new DeviceUsage(System.currentTimeMillis(), d.getId(), time - (i*3600), random));
        }

        return usage;
    }

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
