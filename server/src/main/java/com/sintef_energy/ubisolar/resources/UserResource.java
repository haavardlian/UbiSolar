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
 */
@Path("user/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {
    private  ServerDAO db;

    public UserResource(ServerDAO db) {
        this.db = db;
    }

    @PUT
    public Response createUser(@Valid Token token) {
        int userID = db.createUser(token.getToken());

        if(userID != 0) return Response.status(Response.Status.CREATED).entity(new SimpleJSONMessage(""+userID)).build();
        else throw new WebApplicationException(Response.Status.NOT_MODIFIED);
    }

    @Path("{id}/token")
    @GET
    public SimpleToken getAccessToken(@PathParam("id") IntParam id) {
        SimpleToken token = db.getAccessToken(id.get());

        if(token != null) return token;
        else throw new WebApplicationException(Response.Status.NOT_FOUND);
    }
}
