package org.electricaltrainingalliance.partners.types.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.electricaltrainingalliance.partners.contacts.entity.PartnerContact;
import org.electricaltrainingalliance.validations.ValidEntity;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "contact_types", indexes = {@Index(name = "uk_unique_contact_type", columnList = "type_name", unique = true)})
@NamedQuery(name = ContactType.getAll, query = "SELECT t FROM ContactType t ORDER BY t.typeName")
public class ContactType implements Serializable, ValidEntity {

	private static final long serialVersionUID = 1L;

    private static final String PREFIX = "partners.types.entity.";
    public static final String getAll = PREFIX + "getAll";

    @Id
    @Column(name = "type_id", nullable = false)
    protected UUID typeId;

    @Column(name = "type_name", nullable = false)
    private String typeName;
	
	@JsonIgnore
	@JsonbTransient
	@OneToMany(mappedBy = "type", cascade = CascadeType.ALL, orphanRemoval = true)
    List<PartnerContact> contacts = new ArrayList<>();

    public ContactType() {}

    public ContactType(String name) {
        this.typeName = name;
    }

    public UUID getTypeId() {
        return typeId;
    }

    public void setTypeId(UUID typeId) {
        this.typeId = typeId;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    @Override
    public int hashCode() {
		return Objects.hash(typeId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ContactType other = (ContactType) obj;
        if (typeId == null) {
            if (other.typeId != null)
                return false;
        } else if (!typeId.equals(other.typeId))
            return false;
        return true;
    }
    
    @Override
    @Transient
	@JsonbTransient
    public boolean isValid() {
		// Implement Entity business rules for Validity Checks
        // name is required
        if (this.typeName == null || this.typeName.isEmpty() || this.typeName.isBlank()) return false;
        // all tests pass
        return true;
    }
    
}
