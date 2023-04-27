package org.electricaltrainingalliance.partners.contacts.boundary;

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

import org.electricaltrainingalliance.partners.contacts.entity.PartnerContact;
import org.hibernate.reactive.mutiny.Mutiny.SessionFactory;

import io.smallrye.mutiny.Uni;

@Path("partners/contacts")
@ApplicationScoped
@Produces("application/json")
@Consumes("application/json")
public class PartnerContactsService {

    @Inject
    SessionFactory sf;
    
    @POST
    public Uni<Response> create(PartnerContact contact) {
        if (contact == null || !contact.isValid()) {
            return Uni.createFrom()
                .item(Response.status(400)
                .entity("The Contact is not valid, please refer to the API Documentation.")::build);
        }

        contact.setContactId(UUID.randomUUID());

        return sf.withTransaction((s,t) -> s.persist(contact))
            .replaceWith(Response.ok(contact).status(Status.CREATED)::build)
            .onFailure().invoke(failure -> System.out.println(failure));//new WebApplicationException("A Contact with this information already exists.", Status.CONFLICT));
    }

    @GET
    @Path("{id}")
    public Uni<Response> find(@PathParam("id") UUID id) {
        return sf.withTransaction((s,t) -> s.find(PartnerContact.class, id)
            .onItem().ifNull().failWith(new WebApplicationException("The Contact is missing from database.", Status.NOT_FOUND))
            .map(entity -> Response.ok(entity).build()));
    }

    @GET
    @Path("bypartnerid/{partnerId}")
    public Uni<List<PartnerContact>> findByTrainingPartnerId(@PathParam("parnterId") UUID partnerId) {
        return sf.withTransaction((s,t) -> s
                .createNamedQuery(PartnerContact.findByTrainingPartnerId, PartnerContact.class)
                .setParameter("partnerId", partnerId)
                .getResultList()
        );
    }

    @GET
    @Path("bytypeid/{typeId}")
    public Uni<List<PartnerContact>> findByTypeId(@PathParam("typeId") UUID typeId) {
        return sf.withTransaction((s,t) -> s
                .createNamedQuery(PartnerContact.findByTypeId, PartnerContact.class)
                .setParameter("typeId", typeId)
                .getResultList()
        );
    }

    @GET
    public Uni<List<PartnerContact>> getAll() {
        return sf.withTransaction((s,t) -> s
                .createNamedQuery(PartnerContact.getAll, PartnerContact.class)
                .getResultList()
        );
    }

    @PUT
    @Path("{id}")
    public Uni<Response> update(@PathParam("id") UUID id, PartnerContact contact) {
        if (contact == null || !contact.isValid()) {
            return Uni.createFrom()
                .item(Response.status(400)
                .entity("The Contact is not valid, please refer to the API Documentation.")::build);
        }

        return sf.withTransaction((s,t) -> s.find(PartnerContact.class, id)
            .onItem().ifNull().failWith(new WebApplicationException("Contact missing from database.", Status.NOT_FOUND))
            // If entity exists then update it
            .invoke(entity -> entity.setFirstName(contact.getFirstName()))
            .invoke(entity -> entity.setLastName(contact.getLastName()))
            .invoke(entity -> entity.setTrainingPartner(contact.getTrainingPartner()))
            .invoke(entity -> entity.setType(contact.getType()))
            // optional items
            .invoke(entity -> entity.setPrimaryPhone(contact.getPrimaryPhone() != null ? contact.getPrimaryPhone() : null))
            .invoke(entity -> entity.setMobilePhone(contact.getMobilePhone() != null ? contact.getMobilePhone() : null))
            .invoke(entity -> entity.setEmailAddress(contact.getEmailAddress() != null ? contact.getEmailAddress() : null))
            .map(entity -> Response.ok(entity).build()));
    }

    @DELETE
    @Path("{id}")
    public Uni<Response> delete(@PathParam("id") UUID id) {
        return sf.withTransaction((s,t) -> s.find(PartnerContact.class, id)
            .onItem().ifNull().failWith(new WebApplicationException("Contact missing from database.", Status.NOT_FOUND))
            // If entity exists then delete it
            .call(s::remove))
            .replaceWith(Response.ok().status(Status.NO_CONTENT)::build)
            .onFailure().transform(failure -> new WebApplicationException("This Contact is in use and cannot be removed.", Status.CONFLICT));
    }

}
