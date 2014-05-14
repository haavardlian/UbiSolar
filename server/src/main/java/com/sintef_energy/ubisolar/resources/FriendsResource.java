package com.sintef_energy.ubisolar.resources;

import com.sintef_energy.ubisolar.ServerDAO;
import com.sintef_energy.ubisolar.structs.SimpleJSONMessage;
import com.sintef_energy.ubisolar.structs.Tip;
import com.sintef_energy.ubisolar.structs.TipRating;
import com.sintef_energy.ubisolar.structs.WallPost;
import com.yammer.dropwizard.jersey.params.IntParam;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Håvard on 05.03.14.
 */
@Path("/user/{user}/friends/")
@Consumes("application/json; charset=utf8")
@Produces("application/json; charset=utf8")
public class FriendsResource {
    private final ServerDAO db;
    public FriendsResource(ServerDAO db) {
        this.db = db;
    }

    @GET
    @Path("wall/")
    public List<WallPost> getWallPosts(@QueryParam("friends") String friendIdList) {



        String[] strArray = friendIdList.split(",");
        ArrayList<Integer> intList = new ArrayList<Integer>();
        for(int i = 0; i < strArray.length; i++) {
            intList.add(Integer.parseInt(strArray[i]));
        }

        List<WallPost> feed = db.getWallPostsForFriends(friendIdList);

        if(feed != null && feed.size() > 0) return feed;
        else throw new WebApplicationException(Response.status(Response.Status.NO_CONTENT).entity(new ArrayList<WallPost>()).build());
    }

    @PUT
    @Path("wall/")
    public Response postToWall(@Valid WallPost post) {
        int result = db.createWallPost(post);

        if(result > 0) return Response.status(Response.Status.CREATED).build();
        else return Response.status(Response.Status.NOT_MODIFIED).build();
    }


}
