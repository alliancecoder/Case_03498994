package org.electricaltrainingalliance.partners.contacts.entity;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.electricaltrainingalliance.partners.companies.entity.TrainingPartner;
import org.electricaltrainingalliance.partners.types.entity.ContactType;
import org.electricaltrainingalliance.validations.ValidEntity;

@Entity
@Table(name = "partner_contacts", indexes = {
    @Index(name = "ix_contact_types", columnList = "type_id", unique = false),
    @Index(name = "ix_contact_partners", columnList = "partner_id", unique = false),
    @Index(name = "uk_unique_contact", columnList = "email_address, type_id, partner_id", unique = true)
})
@NamedQueries({
    @NamedQuery(name = PartnerContact.findByTrainingPartnerId, query = "SELECT c FROM PartnerContact c WHERE c.trainingPartner.partnerId = :partnerId ORDER BY c.lastName, c.firstName"),
    @NamedQuery(name = PartnerContact.findByTypeId, query = "SELECT c FROM PartnerContact c WHERE c.type.typeId = :typeId ORDER BY c.lastName, c.firstName"),
    @NamedQuery(name = PartnerContact.getAll, query = "SELECT c FROM PartnerContact c ORDER BY c.lastName, c.firstName"),
})
public class PartnerContact implements Serializable, ValidEntity {

	private static final long serialVersionUID = 1L;

    private static final String PREFIX = "partners.contacts.entity.";
    public static final String findByTrainingPartnerId = PREFIX + "findByTrainingPartnerId";
    public static final String findByTypeId = PREFIX + "findByTypeId";
    public static final String getAll = PREFIX + "getAll";

    @Id
    @Column(name = "contact_id", nullable = false)
    protected UUID contactId;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "primary_phone")
    private String primaryPhone;

    @Column(name = "mobile_phone")
    private String mobilePhone;

    @Column(name = "email_address", nullable = false)
    private String emailAddress;

	@ManyToOne
	@JoinColumn(
        name = "partner_id", 
        referencedColumnName = "partner_id",
        foreignKey = @ForeignKey(name = "fk_contacts_partners"))
	private TrainingPartner trainingPartner;

	@ManyToOne
	@JoinColumn(
        name = "type_id",
        referencedColumnName = "type_id",
        foreignKey = @ForeignKey(name = "fk_contacts_types"))
	private ContactType type;

    //#region GETTERS & SETTERS
   
    public UUID getContactId() {
        return contactId;
    }

    public void setContactId(UUID contactId) {
        this.contactId = contactId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPrimaryPhone() {
        return primaryPhone;
    }

    public void setPrimaryPhone(String primaryPhone) {
        this.primaryPhone = primaryPhone;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public TrainingPartner getTrainingPartner() {
        return trainingPartner;
    }

    public void setTrainingPartner(TrainingPartner trainingPartner) {
        this.trainingPartner = trainingPartner;
    }

    public ContactType getType() {
        return type;
    }

    public void setType(ContactType type) {
        this.type = type;
    }

    //#endregion

    @Override
    public int hashCode() {
		return Objects.hash(contactId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PartnerContact other = (PartnerContact) obj;
        if (contactId == null) {
            if (other.contactId != null)
                return false;
        } else if (!contactId.equals(other.contactId))
            return false;
        return true;
    }

    @Override
    @Transient
	@JsonbTransient
    public boolean isValid() {
		// Implement Entity business rules for Validity Checks
        // training partner is required
        if (this.trainingPartner == null) return false;
        // contact type is required
        if (this.type == null) return false;
        // first name is required
        if (this.firstName == null || this.firstName.isEmpty() || this.firstName.isBlank()) return false;
        // last name is required
        if (this.lastName == null || this.lastName.isEmpty() || this.lastName.isBlank()) return false;
        // email is required
        if (this.emailAddress == null || this.emailAddress.isEmpty() || this.emailAddress.isBlank()) return false;
        // all tests pass
        return true;
    }

}
