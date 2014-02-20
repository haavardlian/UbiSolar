package lol;

/**
 * Created by thb on 12.02.14.
 */
import com.google.common.base.Optional;
import com.sintef_energy.ubisolar.ServerDAO;
import com.yammer.metrics.annotation.Timed;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.concurrent.atomic.AtomicLong;

@Path("/test")
@Produces(MediaType.APPLICATION_JSON)
public class GeneralResource {
    private final AtomicLong counter;
    private final ServerDAO dao;
    public GeneralResource(ServerDAO dao) {
        this.counter = new AtomicLong();
        this.dao = dao;
    }

    @GET
    @Timed
    public GeneralSaying sayAction(@QueryParam("action") Optional<String> action) {
        int i = (int)counter.incrementAndGet();
        return new GeneralSaying(i, "Hei :-)");
    }
}