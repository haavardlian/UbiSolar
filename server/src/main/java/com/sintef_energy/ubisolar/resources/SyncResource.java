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
import com.yammer.dropwizard.jersey.params.IntParam;
import com.yammer.dropwizard.jersey.params.LongParam;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
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

    //@GET
    @Path("/device/newest")
    public long getLastEditedDeviceTime(@PathParam("user") LongParam user) {
        long latest = db.getLastUpdatedTimeDevice(user.get());

        return latest;
    }

    /**
     * Get devices changed after given timestamp
     * @param timestamp The timestamp to check for devices after
     * @param userID The user to check for
     * @return A list of devices edited after the given timestamp
     */
    @GET
    @Path("/device/{timestamp}")
    public List<Device> getNewDevices(@PathParam("timestamp") LongParam timestamp, @PathParam("user") LongParam userID) {
        List<Device> devices = db.getUpdatedDevices(userID.get(), timestamp.get());
        if(devices != null && !devices.isEmpty())
            return devices;
        else
            throw new WebApplicationException(Response.Status.NO_CONTENT);
    }

    /**
     * Creates or updates devices
     * @param devices A list of devices
     * @return A success code or a list of devices that was not modified/created.
     */
    @PUT
    @Path("/device/")
    public Response syncDevices(@Valid ArrayList<Device> devices) {
        int result[] = db.createDevices(devices.iterator());
        boolean success = true;

        ArrayList<Device> failedDevices = new ArrayList<Device>();

        if(result.length != devices.size()) success = false;
        else {
            for(int i = 0; i < result.length; i++) {
                if(result[i] == 0) {
                    success = false;
                    failedDevices.add(devices.get(i));
                }
            }
        }
        if(success)
            throw new WebApplicationException(Response.Status.CREATED);
        else
            return Response.status(Response.Status.NOT_ACCEPTABLE).entity(failedDevices).build();
    }


    //@GET
    @Path("/usage/newest")
    public long getLastEditedUsageTime(@PathParam("user") IntParam user) {
        long latest = db.getLastUpdatedTimeUsage(user.get());

        return latest;
    }

    /**
     * Get usage elements changed after given timestamp
     * @param timestamp The timestamp to check for usage elements after
     * @param userID The user to check for
     * @return A list of usage elements edited after the given timestamp
     */
    @GET
    @Path("/usage/{timestamp}")
    public List<DeviceUsage> getUpdatedUsage(@PathParam("timestamp") LongParam timestamp,
                                             @PathParam("user") LongParam userID) {
        List<DeviceUsage> usage = db.getUpdatedUsage(userID.get(), timestamp.get());
        if(usage != null && !usage.isEmpty())
            return usage;
        else
            throw new WebApplicationException(Response.Status.NO_CONTENT);
    }

    /**
     * Creates or updates usage
     * @param usage A list of usage
     * @return A success code or a list of usage that was not modified/created.
     */
    @PUT
    @Path("/usage/")
    public Response syncUsage(@Valid ArrayList<DeviceUsage> usage) {
        int result[] = db.addUsageForDevices(usage.iterator());
        boolean success = true;

        ArrayList<DeviceUsage> failedUsages = new ArrayList<DeviceUsage>();

        if(result.length != usage.size()) success = false;
        else {
            for(int i = 0; i < result.length; i++) {
                if(result[i] == 0) {
                    success = false;
                    failedUsages.add(usage.get(i));
                }
            }
        }
        if(success)
            throw new WebApplicationException(Response.Status.CREATED);
        else
            return Response.status(Response.Status.NOT_ACCEPTABLE).entity(failedUsages).build();
    }
}
