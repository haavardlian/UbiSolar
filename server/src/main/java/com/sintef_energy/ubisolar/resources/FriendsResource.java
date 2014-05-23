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
