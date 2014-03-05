package com.sintef_energy.ubisolar.resources;

import com.sintef_energy.ubisolar.ServerDAO;
import com.sintef_energy.ubisolar.structs.SimpleJSONMessage;
import com.sintef_energy.ubisolar.structs.Tip;
import com.sintef_energy.ubisolar.structs.TipRating;
import com.yammer.dropwizard.jersey.params.IntParam;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created by Håvard on 05.03.14.
 */
@Path("/tips")
@Consumes("application/json; charset=utf8")
@Produces("application/json; charset=utf8")
public class TipsResource {
    private final ServerDAO db;
    public TipsResource(ServerDAO db) {
        this.db = db;
    }

    @GET
    public List<Tip> getAllTips() {
        List<Tip> tips = db.getAllTips();

        if(tips != null && !tips.isEmpty()) return tips;
        else throw new WebApplicationException(Response.Status.NOT_FOUND);
    }

    @GET
    @Path("/{id}")
    public Tip getTipById(@PathParam("id")IntParam id) {
        Tip tip = db.getTipById(id.get());

        if(tip != null) return tip;
        else throw new WebApplicationException(Response.Status.NOT_FOUND);
    }

    @GET
    @Path("/{id}/rating/")
    public List<TipRating> getRatingsForTip(@PathParam("id") IntParam id) {
        List<TipRating> ratings = db.getRatingsForTip(id.get());

        if(ratings != null && !ratings.isEmpty()) return ratings;
        else throw new WebApplicationException(Response.Status.NOT_FOUND);

    }

    @PUT
    @Path("/{id}/rating")
    public Response createRating(@Valid TipRating rating) {
        int result = db.createRating(rating);

        if(result == 1) return Response.status(Response.Status.CREATED).entity(new SimpleJSONMessage("Rating created")).build();
        else throw new WebApplicationException(Response.Status.NOT_MODIFIED);
    }

    @PUT
    public Response createTip(@Valid Tip tip) {
        int result = db.createTip(tip);

        if(result == 1) return Response.status(Response.Status.CREATED).entity(new SimpleJSONMessage("Tip created")).build();
        else throw new WebApplicationException(Response.Status.NOT_MODIFIED);
    }
}
