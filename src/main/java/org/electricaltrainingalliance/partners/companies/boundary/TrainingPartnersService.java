package org.electricaltrainingalliance.partners.companies.boundary;

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

import org.electricaltrainingalliance.partners.companies.entity.TrainingPartner;
import org.hibernate.reactive.mutiny.Mutiny.SessionFactory;

import io.smallrye.mutiny.Uni;

@Path("partners")
@ApplicationScoped
@Produces("application/json")
@Consumes("application/json")
public class TrainingPartnersService {

    @Inject
    SessionFactory sf;
    
    @POST
    public Uni<Response> create(TrainingPartner partner) {
        if (partner == null || !partner.isValid()) {
            return Uni.createFrom()
                .item(Response.status(400)
                .entity("The Training Partner is not valid, please refer to the API Documentation.")::build);
        }

        partner.setPartnerId(UUID.randomUUID());

        return sf.withTransaction((s,t) -> s.persist(partner))
            .replaceWith(Response.ok(partner).status(Status.CREATED)::build)
            .onFailure().transform(failure -> new WebApplicationException("A Training Partner with this information already exists.", Status.CONFLICT));
    }

    @GET
    @Path("{id}")
    public Uni<Response> find(@PathParam("id") UUID id) {
        return sf.withTransaction((s,t) -> s.find(TrainingPartner.class, id)
            .onItem().ifNull().failWith(new WebApplicationException("The Training Partner is missing from database.", Status.NOT_FOUND))
            .map(entity -> Response.ok(entity).build()));
    }

    @GET
    public Uni<List<TrainingPartner>> getAll() {
        return sf.withTransaction((s,t) -> s
                .createNamedQuery(TrainingPartner.getAll, TrainingPartner.class)
                .getResultList()
        );
    }

    @PUT
    @Path("{id}")
    public Uni<Response> update(@PathParam("id") UUID id, TrainingPartner partner) {
        if (partner == null || !partner.isValid()) {
            return Uni.createFrom()
                .item(Response.status(400)
                .entity("The Training Partner is not valid, please refer to the API Documentation.")::build);
        }

        return sf.withTransaction((s,t) -> s.find(TrainingPartner.class, id)
            .onItem().ifNull().failWith(new WebApplicationException("Training Partner missing from database.", Status.NOT_FOUND))
            // If entity exists then update it
            .invoke(entity -> entity.setPartnerName(partner.getPartnerName()))
            .map(entity -> Response.ok(entity).build()));
    }

    @DELETE
    @Path("{id}")
    public Uni<Response> delete(@PathParam("id") UUID id) {
        return sf.withTransaction((s,t) -> s.find(TrainingPartner.class, id)
            .onItem().ifNull().failWith(new WebApplicationException("Training Partner missing from database.", Status.NOT_FOUND))
            // If entity exists then delete it
            .call(s::remove))
            .replaceWith(Response.ok().status(Status.NO_CONTENT)::build)
            .onFailure().transform(failure -> new WebApplicationException("This Training Partner is in use and cannot be removed.", Status.CONFLICT));
    }
}
