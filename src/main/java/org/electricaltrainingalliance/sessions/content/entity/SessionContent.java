package org.electricaltrainingalliance.sessions.content.entity;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.electricaltrainingalliance.sessions.types.entity.SessionType;
import org.electricaltrainingalliance.validations.ValidEntity;

@Entity
@Table(name = "session_content", indexes = { @Index(name = "uk_unique_content", columnList = "content_name", unique = true) })
@NamedQuery(name = SessionContent.getAll, query = "SELECT c FROM SessionContent c ORDER BY c.name")
public class SessionContent implements Serializable, ValidEntity {

	private static final long serialVersionUID = 1L;

    private static final String PREFIX = "sessions.content.entity.";
    public static final String getAll = PREFIX + "getAll";

    @Id
    @Column(name = "content_id", nullable = false)
    protected UUID contentId;

    @Column(name = "content_name", nullable = false)
    private String name;

    @Column(name = "content_description", nullable = false)
    private String description;

	@ManyToOne
	@JoinColumn(name = "type_id", referencedColumnName = "type_id", nullable = false, foreignKey = @ForeignKey(name = "fk_content_type"))
	private SessionType type;

    public UUID getContentId() {
        return contentId;
    }

    public void setContentId(UUID contentId) {
        this.contentId = contentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public int hashCode() {
        return Objects.hash(contentId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SessionContent other = (SessionContent) obj;
        if (contentId == null) {
            if (other.contentId != null)
                return false;
        } else if (!contentId.equals(other.contentId))
            return false;
        return true;
    }

    @Override
	@Transient
	@JsonbTransient
	public boolean isValid() {
		// Implement Entity business rules for Validity Checks
        // type is required
        if (this.type == null) return false;
        // name is required
        if (this.name == null || this.name.isEmpty() || this.name.isBlank()) return false;
        // description is required
        if (this.description == null || this.description.isEmpty() || this.description.isBlank()) return false;
        // all tests passed
		return true;
	}
    
}
