package com.sintef_energy.ubisolar.resources;

import com.sintef_energy.ubisolar.ServerDAO;
import com.sintef_energy.ubisolar.structs.WallPost;

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

    /**
     * Get wall post created by a users friends
     * @param friendIds A list of friend ids
     * @return A list of wall posts created by friends
     */
    @GET
    @Path("wall/")
    public List<WallPost> getWallPosts(@QueryParam("friends") String friendIds) {



        ArrayList<String> friendList = new ArrayList<String>();
        String[] friendIdArray = friendIds.split(",");
        for(int i = 0; i < friendIdArray.length; i++) {
            friendList.add(friendIdArray[i]);
        }

        List<WallPost> feed = db.getWallPostsForFriends(friendList);

        if(feed != null && feed.size() > 0) return feed;
        else throw new WebApplicationException(Response.status(Response.Status.NO_CONTENT).entity(
                new ArrayList<WallPost>()).build());
    }

    /**
     * Create a post
     * @param post The post
     * @return A success or error code
     */
    @PUT
    @Path("wall/")
    public Response postToWall(@Valid WallPost post) {
        int result = db.createWallPost(post);

        if(result > 0) return Response.status(Response.Status.CREATED).build();
        else return Response.status(Response.Status.NOT_MODIFIED).build();
    }


}
