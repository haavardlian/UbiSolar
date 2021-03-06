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
import com.sintef_energy.ubisolar.structs.SimpleToken;
import com.yammer.dropwizard.jersey.params.IntParam;
import org.scribe.model.Token;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by Håvard on 26.03.14.
 *
 * Resources related to users
 */
@Path("user/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {
    private  ServerDAO db;

    public UserResource(ServerDAO db) {
        this.db = db;
    }

    /**
     * Create a user in the database
     * @param token The facebook access token
     * @return On success return the facebook user id else return an error code
     */
    @PUT
    public Response createUser(@Valid Token token) {
        int userID = db.createUser(token.getToken());

        if(userID != 0) return Response.status(Response.Status.CREATED).entity(
                new SimpleJSONMessage(""+userID)).build();
        else throw new WebApplicationException(Response.Status.NOT_MODIFIED);
    }

    /**
     * Gets the facebook access token for a facebook user id
     * @param id The facebook user id
     * @return On success return the token else return an error code
     */
    @Path("{id}/token")
    @GET
    public SimpleToken getAccessToken(@PathParam("id") IntParam id) {
        SimpleToken token = db.getAccessToken(id.get());

        if(token != null) return token;
        else throw new WebApplicationException(Response.Status.NOT_FOUND);
    }
}
