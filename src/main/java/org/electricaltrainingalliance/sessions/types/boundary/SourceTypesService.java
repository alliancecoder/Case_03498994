package org.electricaltrainingalliance.sessions.types.boundary;

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

import org.electricaltrainingalliance.sessions.types.entity.SessionType;
import org.hibernate.reactive.mutiny.Mutiny.SessionFactory;

import io.smallrye.mutiny.Uni;


@Path("sessions/types")
@ApplicationScoped
@Produces("application/json")
@Consumes("application/json")
public class SourceTypesService {

	// private static final Logger LOGGER = Logger.getLogger(SourceTypesService.class.getName());

    @Inject
    SessionFactory sf;
    
    @POST
    public Uni<Response> create(SessionType type) {
        if (type == null || !type.isValid()) {
            return Uni.createFrom()
                .item(Response.status(400)
                .entity("The Session Type is not valid, please refer to the API Documentation.")::build);
        }

        type.setTypeId(UUID.randomUUID());

        return sf.withTransaction((s,t) -> s.persist(type))
            .replaceWith(Response.ok(type).status(Status.CREATED)::build)
            .onFailure().transform(failure -> new WebApplicationException("A Type with this name already exists.", Status.CONFLICT));
    }

    @GET
    @Path("{id}")
    public Uni<Response> find(@PathParam("id") UUID id) {
        return sf.withTransaction((s,t) -> s.find(SessionType.class, id)
            .onItem().ifNull().failWith(new WebApplicationException("The Session Type is missing from database.", Status.NOT_FOUND))
            .map(entity -> Response.ok(entity).build()));
    }

    @GET
    public Uni<List<SessionType>> getAll() {
        return sf.withTransaction((s,t) -> s
                .createNamedQuery(SessionType.getAll, SessionType.class)
                .getResultList()
        );
    }

    @PUT
    @Path("{id}")
    public Uni<Response> update(@PathParam("id") UUID id, SessionType type) {
        if (type == null || !type.isValid()) {
            return Uni.createFrom()
                .item(Response.status(400)
                .entity("The Session Type is not valid, please refer to the API Documentation.")::build);
        }

        return sf.withTransaction((s,t) -> s.find(SessionType.class, id)
            .onItem().ifNull().failWith(new WebApplicationException("Session Type missing from database.", Status.NOT_FOUND))
            // If entity exists then update it
            .invoke(entity -> entity.setTypeName(type.getTypeName()))
            .map(entity -> Response.ok(entity).build()));
    }

    @DELETE
    @Path("{id}")
    public Uni<Response> delete(@PathParam("id") UUID id) {
        return sf.withTransaction((s,t) -> s.find(SessionType.class, id)
            .onItem().ifNull().failWith(new WebApplicationException("Session Type missing from database.", Status.NOT_FOUND))
            // If entity exists then delete it
            .call(s::remove))
            .replaceWith(Response.ok().status(Status.NO_CONTENT)::build)
            .onFailure().transform(failure -> new WebApplicationException("This Type is in use and cannot be removed.", Status.CONFLICT));
    }
}
