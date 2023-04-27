package org.electricaltrainingalliance.sessions.content.boundary;

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

import org.electricaltrainingalliance.sessions.content.entity.SessionContent;
import org.hibernate.reactive.mutiny.Mutiny.SessionFactory;

import io.smallrye.mutiny.Uni;

@Path("content")
@ApplicationScoped
@Produces("application/json")
@Consumes("application/json")
public class SessionContentService {

    @Inject
    SessionFactory sf;
    
    @POST
    public Uni<Response> create(SessionContent content) {
        if (content == null || !content.isValid()) {
            return Uni.createFrom()
                .item(Response.status(400)
                .entity("The Content is not valid, please refer to the API Documentation.")::build);
        }

        content.setContentId(UUID.randomUUID());

        return sf.withTransaction((s,t) -> s.persist(content))
            .replaceWith(Response.ok(content).status(Status.CREATED)::build)
            .onFailure().transform(failure -> new WebApplicationException("A Contact with this information already exists.", Status.CONFLICT));
    }

    @GET
    @Path("{id}")
    public Uni<Response> find(@PathParam("id") UUID id) {
        return sf.withTransaction((s,t) -> s.find(SessionContent.class, id)
            .onItem().ifNull().failWith(new WebApplicationException("The Content is missing from database.", Status.NOT_FOUND))
            .map(entity -> Response.ok(entity).build()));
    }

    @GET
    public Uni<List<SessionContent>> getAll() {
        return sf.withTransaction((s,t) -> s
                .createNamedQuery(SessionContent.getAll, SessionContent.class)
                .getResultList()
        );
    }

    @PUT
    @Path("{id}")
    public Uni<Response> update(@PathParam("id") UUID id, SessionContent content) {
        if (content == null || !content.isValid()) {
            return Uni.createFrom()
                .item(Response.status(400)
                .entity("The Content is not valid, please refer to the API Documentation.")::build);
        }

        return sf.withTransaction((s,t) -> s.find(SessionContent.class, id)
            .onItem().ifNull().failWith(new WebApplicationException("The Content is missing from database.", Status.NOT_FOUND))
            // If entity exists then update it
            .invoke(entity -> entity.setName(content.getName()))
            .invoke(entity -> entity.setDescription(content.getDescription()))
            .map(entity -> Response.ok(entity).build()));
    }

    @DELETE
    @Path("{id}")
    public Uni<Response> delete(@PathParam("id") UUID id) {
        return sf.withTransaction((s,t) -> s.find(SessionContent.class, id)
            .onItem().ifNull().failWith(new WebApplicationException("The Content is missing from database.", Status.NOT_FOUND))
            // If entity exists then delete it
            .call(s::remove))
            .replaceWith(Response.ok().status(Status.NO_CONTENT)::build)
            .onFailure().transform(failure -> new WebApplicationException("This Content is in use and cannot be removed.", Status.CONFLICT));
    }

}
