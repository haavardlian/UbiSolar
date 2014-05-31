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
import com.sintef_energy.ubisolar.structs.DeviceUsage;
import com.sintef_energy.ubisolar.structs.SimpleJSONMessage;
import com.sintef_energy.ubisolar.structs.TotalUsage;
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
@Path("user/{user}/usage/devices")
@Produces(MediaType.APPLICATION_JSON)
public class DeviceUsageResource {
    private final ServerDAO db;

    public DeviceUsageResource(ServerDAO db) {
        this.db = db;
    }

    @GET
    @Path("/total/{interval}/")
    public List<TotalUsage> getTotalDeviceUsage(@PathParam("user") IntParam user,
                                                @PathParam("interval") String interval) {
        List<TotalUsage> totalUsage = null;

        if(interval.equals("yearly"))
            totalUsage = db.getTotalDevicesUsageYearly(user.get());
        else if(interval.equals("monthly"))
            totalUsage = db.getTotalDevicesUsageMonthly(user.get());
        else if(interval.equals("daily"))
            totalUsage = db.getTotalDevicesUsageDaily(user.get());
        else
            totalUsage = db.getTotalDevicesUsageMonthly(user.get());

        if(totalUsage != null && !totalUsage.isEmpty())
            return totalUsage;
        else
            throw new WebApplicationException(Response.Status.NOT_FOUND);
    }

    @GET
    @Path("/{device}/")
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
        int result;
        if(device.get() == usage.getDeviceId()) result  = db.addUsageForDevice(usage);
        else result = 0;

        if(result == 1) return  Response.status(Response.Status.CREATED).entity(
                new SimpleJSONMessage("Usage added")).build();
        else throw new WebApplicationException(Response.Status.NOT_MODIFIED);
    }
}
