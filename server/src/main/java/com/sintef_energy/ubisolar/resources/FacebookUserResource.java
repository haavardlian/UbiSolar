package com.sintef_energy.ubisolar.resources;

import com.sintef_energy.ubisolar.ServerDAO;
import com.sintef_energy.ubisolar.structs.Device;
import com.sintef_energy.ubisolar.structs.FacebookUser;
import com.sintef_energy.ubisolar.structs.SimpleJSONMessage;
import com.yammer.dropwizard.jersey.params.IntParam;
import com.yammer.dropwizard.jersey.params.LongParam;
import com.yammer.metrics.annotation.Timed;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lars Erik on 03.05.14.
 */

@Path("/facebookuser")
@Produces("application/json; charset=utf8")
@Consumes("application/json; charset=utf8")

public class FacebookUserResource {

    private final ServerDAO db;

    public FacebookUserResource(ServerDAO db) {
        this.db = db;
    }

    @PUT
    @Timed
    public Response createFacebookUser(@Valid FacebookUser user) {
        int result = db.createFacebookUser(user);

        if(result == 1) return  Response.status(Response.Status.CREATED).entity(new SimpleJSONMessage("User created")).build();
        else throw new WebApplicationException(Response.Status.NOT_MODIFIED);
    }

    @GET
    public List<FacebookUser> getAllFacebookUsers() {
        List<FacebookUser> fbUsers = db.getAllFacebookUsers();
        if(fbUsers != null) return fbUsers;
        else throw new WebApplicationException(Response.Status.NOT_FOUND);
    }

    @Path("/{user}")
    @DELETE
    public Response deleteUserById(@PathParam("user") IntParam user) {
        int result = db.deleteFacebookUserById(user.get());

        if(result == 1) return Response.ok(new SimpleJSONMessage("User deleted")).build();
        else throw new WebApplicationException(Response.Status.NOT_MODIFIED);
    }

    @Path("/{user}")
    @GET
    public FacebookUser getUserById(@PathParam("user") IntParam user) {
        FacebookUser fbUser = db.getFacebookUserById(user.get());
        if(user != null) return fbUser;
        else throw new WebApplicationException(Response.Status.NOT_FOUND);
    }

    @Path("/friends/")
    @GET
    public List<FacebookUser> getFacebookFriends(@Valid ArrayList<FacebookUser> users) {
        List<FacebookUser> fbUsers = db.getAllFacebookUsers();
        List<FacebookUser> result = new ArrayList<FacebookUser>();

        for(FacebookUser listedUser : fbUsers) {
            if(users.contains(listedUser))
                result.add(listedUser);
        }

        if(fbUsers != null) return result;
        else throw new WebApplicationException(Response.Status.NOT_FOUND);
    }

}
