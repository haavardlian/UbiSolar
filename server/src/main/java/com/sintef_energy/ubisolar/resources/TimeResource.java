package com.sintef_energy.ubisolar.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

/**
 * Created by Håvard on 02.05.14.
 */
@Path("/time")
public class TimeResource {

    @GET
    long getCurrentTime()
    {
        return System.currentTimeMillis();
    }

}
