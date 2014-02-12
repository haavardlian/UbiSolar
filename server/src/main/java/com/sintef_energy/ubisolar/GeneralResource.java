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
    private final ServerDAO dao;
    public GeneralResource(ServerDAO dao) {
        this.defaultName = "Test";
        this.counter = new AtomicLong();
        this.dao = dao;
    }

    @GET
    @Timed
    public GeneralSaying sayHello(@QueryParam("name") Optional<String> name) {
        int i = (int)counter.incrementAndGet();
        if(name.get().equals("create")) {
            dao.createTable();
            return new GeneralSaying(i, "Created table");
        }
        else if(name.get().equals("insert")) {
            dao.insert(i, "Hello");
            return new GeneralSaying(i, "Insert");
        }
        else if(name.get().equals("get"))
            return new GeneralSaying(i, "Got: " + dao.findNameById(i-1));
        else
            return new GeneralSaying(i, name.get());

    }
}