package com.sintef_energy.ubisolar.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Created by Håvard on 02.05.14.
 */
@Path("/time")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TimeResource {

    /**
     * Returns the current time on the server
     * @return Current time in milliseconds since 1. Jan 1970
     */
    @GET
    public long getCurrentTime() {
        return System.currentTimeMillis();
    }

}
