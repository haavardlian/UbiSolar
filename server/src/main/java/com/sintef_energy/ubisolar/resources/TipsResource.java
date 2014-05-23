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
import com.sintef_energy.ubisolar.structs.SimpleJSONMessage;
import com.sintef_energy.ubisolar.structs.Tip;
import com.sintef_energy.ubisolar.structs.TipRating;
import com.yammer.dropwizard.jersey.params.IntParam;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created by Håvard on 05.03.14.
 *
 * Resource for power saving tips
 */
@Path("/tips")
@Consumes("application/json; charset=utf8")
@Produces("application/json; charset=utf8")
public class TipsResource {
    private final ServerDAO db;
    public TipsResource(ServerDAO db) {
        this.db = db;
    }

    /**
     * Get all tips from the database
     * @return A list of tips
     */
    @GET
    public List<Tip> getAllTips() {
        List<Tip> tips = db.getAllTips();

        if(tips != null && !tips.isEmpty()) return tips;
        else throw new WebApplicationException(Response.Status.NOT_FOUND);
    }

    @GET
    @Path("/{id}")
    public Tip getTipById(@PathParam("id")IntParam id) {
        Tip tip = db.getTipById(id.get());

        if(tip != null) return tip;
        else throw new WebApplicationException(Response.Status.NOT_FOUND);
    }

    /**
     * Gets a specific tip determined by id
     * @param id Tip id
     * @return The tip corresponding to the id if it exists
     */
    @GET
    @Path("/{id}/rating/")
    public List<TipRating> getRatingsForTip(@PathParam("id") IntParam id) {
        List<TipRating> ratings = db.getRatingsForTip(id.get());

        if(ratings != null && !ratings.isEmpty()) return ratings;
        else throw new WebApplicationException(Response.Status.NOT_FOUND);

    }

    /**
     * Create a rating for a tip
     * @param rating The tip rating
     * @return A success or error message depending on whether the rating was created
     */
    @PUT
    @Path("/{id}/rating")
    public Response createRating(@Valid TipRating rating) {
        int result = db.createRating(rating);
        System.out.println("Changed: " + result);
        if(result > 0) return Response.status(Response.Status.CREATED).entity(
                new SimpleJSONMessage("Rating created")).build();
        else throw new WebApplicationException(Response.Status.NOT_MODIFIED);
    }

    /**
     * Create a tip
     * @param tip The tip
     * @return A success or error message depending on whether the tip was created
     */
    @PUT
    public Response createTip(@Valid Tip tip) {
        int result = db.createTip(tip);

        if(result == 1) return Response.status(Response.Status.CREATED).entity(
                new SimpleJSONMessage("Tip created")).build();
        else throw new WebApplicationException(Response.Status.NOT_MODIFIED);
    }
}
