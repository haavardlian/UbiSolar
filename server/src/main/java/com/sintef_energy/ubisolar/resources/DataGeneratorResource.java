/*
 * Licensed to UbiCollab.org under one or more contributor
 * license agreements.  See the NOTICE file distributed
 * with this work for additional information regarding
 * copyright ownership. UbiCollab.org licenses this file
 * to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

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

    long n = 125363;

    public DataGeneratorResource(ServerDAO db) {
        this.db = db;
    }

    /**
     * Create some devices
     * @param user User id
     * @return A list of devices for a user
     */
    private ArrayList<Device> generateDevices(long user) {
        long time = System.currentTimeMillis();
        ArrayList<Device> devices = new ArrayList<Device>();

        devices.add(new Device(time+1, user, "TV", "This is your TV", time/1000l, false, 1));
        devices.add(new Device(time+2, user, "Stove", "This is your Stove", time/1000l, false, 2));
        devices.add(new Device(time+3, user, "Fridge", "This is your Fridge", time/1000l, false, 3));
        devices.add(new Device(time+4, user, "Heater", "This is your Heater", time/1000l, false, 4));

        return devices;
    }

    /**
     * Generate usage for given device
     * @param d The device
     * @return A list of usage for a device
     */
    private ArrayList<DeviceUsage> generateUsage(Device d) {
        ArrayList<DeviceUsage> usage = new ArrayList<DeviceUsage>();
        Random r = new Random();

        long time = System.currentTimeMillis();
        double rangeMin = 5.0, rangeMax = 20.0;

        double random;

        for(int i = 0; i < 100; i++) {
            random = rangeMin + (rangeMax - rangeMin) * r.nextDouble();
            rangeMin = random - 5;
            rangeMax = random + 5;
            if(random < 0) random = -random;
            usage.add(new DeviceUsage(n++, d.getId(), (time/1000L) - (i*172800), random, false, (time/1000L)));
        }


        return usage;
    }

    /**
     * Generate data for a user
     * @param user the user id
     * @return OK status code
     */
    @GET
    @Path("generate/")
    public Response generateData(@PathParam("user") LongParam user) {
        ArrayList<DeviceUsage> usage;
        ArrayList<Device> devices = this.generateDevices(user.get());
        db.createDevices(devices.iterator());

        for(Device d : devices) {
            usage = generateUsage(d);

            db.addUsageForDevices(usage.iterator());
        }

        return Response.status(Response.Status.OK).build();
    }

}
