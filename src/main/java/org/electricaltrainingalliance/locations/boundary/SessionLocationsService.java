package org.electricaltrainingalliance.locations.boundary;

import java.util.List;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.electricaltrainingalliance.locations.entity.SessionLocation;
import org.hibernate.reactive.mutiny.Mutiny.SessionFactory;

import io.smallrye.mutiny.Uni;

@Path("locations")
@ApplicationScoped
@Produces("application/json")
@Consumes("application/json")
public class SessionLocationsService {

    @Inject
    SessionFactory sf;
    
    @POST
    public Uni<Response> create(SessionLocation location) {
        if (location == null || !location.isValid()) {
            return Uni.createFrom()
                .item(Response.status(400)
                .entity("The Location is not valid, please refer to the API Documentation.")::build);
        }

        location.setLocationId(UUID.randomUUID());

        return sf.withTransaction((s,t) -> s.persist(location))
            .replaceWith(Response.ok(location).status(Status.CREATED)::build)
            .onFailure().transform(failure -> new WebApplicationException("A Location with this information already exists.", Status.CONFLICT));
    }

    @GET
    @Path("{id}")
    public Uni<Response> find(@PathParam("id") UUID id) {
        return sf.withTransaction((s,t) -> s.find(SessionLocation.class, id)
            .onItem().ifNull().failWith(new WebApplicationException("The Location is missing from database.", Status.NOT_FOUND))
            .map(entity -> Response.ok(entity).build()));
    }

    @GET
    @Path("bybuilding/{building}")
    public Uni<List<SessionLocation>> findByTrainingPartnerId(@PathParam("building") String building) {
        return sf.withTransaction((s,t) -> s
                .createNamedQuery(SessionLocation.findByBuilding, SessionLocation.class)
                .setParameter("building", building)
                .getResultList()
        );
    }

    @GET
    public Uni<List<SessionLocation>> getAll() {
        return sf.withTransaction((s,t) -> s
                .createNamedQuery(SessionLocation.getAll, SessionLocation.class)
                .getResultList()
        );
    }

    @PUT
    @Path("{id}")
    public Uni<Response> update(@PathParam("id") UUID id, SessionLocation location) {
        if (location == null || !location.isValid()) {
            return Uni.createFrom()
                .item(Response.status(400)
                .entity("The Location is not valid, please refer to the API Documentation.")::build);
        }

        return sf.withTransaction((s,t) -> s.find(SessionLocation.class, id)
            .onItem().ifNull().failWith(new WebApplicationException("Location missing from database.", Status.NOT_FOUND))
            // If entity exists then update it
            .invoke(entity -> entity.setBuildingName(location.getBuildingName()))
            .invoke(entity -> entity.setRoom(location.getRoom()))
            .map(entity -> Response.ok(entity).build()));
    }

    @DELETE
    @Path("{id}")
    public Uni<Response> delete(@PathParam("id") UUID id) {
        return sf.withTransaction((s,t) -> s.find(SessionLocation.class, id)
            .onItem().ifNull().failWith(new WebApplicationException("Location missing from database.", Status.NOT_FOUND))
            // If entity exists then delete it
            .call(s::remove))
            .replaceWith(Response.ok().status(Status.NO_CONTENT)::build)
            .onFailure().transform(failure -> new WebApplicationException("This Location is in use and cannot be removed.", Status.CONFLICT));
    }

}
