package org.electricaltrainingalliance.partners.companies.entity;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.electricaltrainingalliance.validations.ValidEntity;

@Entity
@Table(name = "training_partners", indexes = { @Index(name = "uk_unique_training_partner", columnList = "partner_name", unique = true)})
@NamedQuery(name = TrainingPartner.getAll, query = "SELECT p FROM TrainingPartner p ORDER BY p.partnerName")
public class TrainingPartner implements Serializable, ValidEntity {

	private static final long serialVersionUID = 1L;

    private static final String PREFIX = "partners.companies.entity.";
    public static final String getAll = PREFIX + "getAll";

    @Id
    @Column(name = "partner_id", nullable = false)
    protected UUID partnerId;

    @Column(name = "partner_name", nullable = false)
    private String partnerName;

    public TrainingPartner() {}

    public TrainingPartner(String name) {
        this.partnerName = name;
    }

    public UUID getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(UUID partnerId) {
        this.partnerId = partnerId;
    }

    public String getPartnerName() {
        return partnerName;
    }

    public void setPartnerName(String partnerName) {
        this.partnerName = partnerName;
    }

    @Override
    public int hashCode() {
		return Objects.hash(partnerId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TrainingPartner other = (TrainingPartner) obj;
        if (partnerId == null) {
            if (other.partnerId != null)
                return false;
        } else if (!partnerId.equals(other.partnerId))
            return false;
        return true;
    }

    @Override
    @Transient
	@JsonbTransient
    public boolean isValid() {
		// Implement Entity business rules for Validity Checks
        // name is required
        if (this.partnerName == null || this.partnerName.isEmpty() || this.partnerName.isBlank()) return false;
        // all tests pass
        return true;
    }
    
}
