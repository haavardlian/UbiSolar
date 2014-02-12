package com.sintef_energy.ubisolar;

/**
 * Created by thb on 12.02.14.
 */
import com.google.common.base.Optional;
import com.yammer.metrics.annotation.Timed;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.concurrent.atomic.AtomicLong;

@Path("/hello-world")
@Produces(MediaType.APPLICATION_JSON)
public class GeneralResource {
    private final String defaultName;
    private final AtomicLong counter;

    public GeneralResource(String defaultName) {
        this.defaultName = defaultName;
        this.counter = new AtomicLong();
    }

    @GET
    @Timed
    public GeneralSaying sayHello(@QueryParam("name") Optional<String> name) {
        return new GeneralSaying(counter.incrementAndGet(), name.or(defaultName));
    }
}