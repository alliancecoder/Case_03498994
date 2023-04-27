package org.electricaltrainingalliance.sessions.slots.boundary;

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

import org.electricaltrainingalliance.sessions.slots.entity.TimeSlot;
import org.electricaltrainingalliance.sessions.types.entity.SessionType;
import org.hibernate.reactive.mutiny.Mutiny.SessionFactory;

import io.smallrye.mutiny.Uni;

@Path("timeslots")
@ApplicationScoped
@Produces("application/json")
@Consumes("application/json")
public class TimeSlotService {

    @Inject
    SessionFactory sf;
    
    @POST
    public Uni<Response> create(TimeSlot slot) {
        if (slot == null || !slot.isValid()) {
            return Uni.createFrom()
                .item(Response.status(400)
                .entity("The Time Slot is not valid, please refer to the API Documentation.")::build);
        }

        slot.setSlotId(UUID.randomUUID());

        return sf.withTransaction((s,t) -> s.persist(slot))
            .replaceWith(Response.ok(slot).status(Status.CREATED)::build)
            .onFailure().transform(failure -> new WebApplicationException("A Slot with this date and time already exists.", Status.CONFLICT));
    }

    @GET
    @Path("{id}")
    public Uni<Response> find(@PathParam("id") UUID id) {
        return sf.withTransaction((s,t) -> s.find(TimeSlot.class, id)
            .onItem().ifNull().failWith(new WebApplicationException("The Time Slot is missing from database.", Status.NOT_FOUND))
            .map(entity -> Response.ok(entity).build()));
    }

    @GET
    public Uni<List<TimeSlot>> getAll() {
        return sf.withTransaction((s,t) -> s
                .createNamedQuery(TimeSlot.getAll, TimeSlot.class)
                .getResultList()
        );
    }

    @PUT
    @Path("{id}")
    public Uni<Response> update(@PathParam("id") UUID id, TimeSlot slot) {
        if (slot == null || !slot.isValid()) {
            return Uni.createFrom()
                .item(Response.status(400)
                .entity("The Time Slot is not valid, please refer to the API Documentation.")::build);
        }

        return sf.withTransaction((s,t) -> s.find(TimeSlot.class, id)
            .onItem().ifNull().failWith(new WebApplicationException("The Time Slot missing from the database.", Status.NOT_FOUND))
            // If entity exists then update it
            .invoke(entity -> entity.setDay(slot.getDay()))
            .invoke(entity -> entity.setSlotDate(slot.getSlotDate()))
            .invoke(entity -> entity.setSlotTime(slot.getSlotTime()))
            .map(entity -> Response.ok(entity).build()));
    }

    @DELETE
    @Path("{id}")
    public Uni<Response> delete(@PathParam("id") UUID id) {
        return sf.withTransaction((s,t) -> s.find(SessionType.class, id)
            .onItem().ifNull().failWith(new WebApplicationException("Time Slot missing from database.", Status.NOT_FOUND))
            // If entity exists then delete it
            .call(s::remove))
            .replaceWith(Response.ok().status(Status.NO_CONTENT)::build)
            .onFailure().transform(failure -> new WebApplicationException("This Type is in use and cannot be removed.", Status.CONFLICT));
    }
    
}
