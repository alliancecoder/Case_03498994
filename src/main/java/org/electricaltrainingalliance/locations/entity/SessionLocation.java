package org.electricaltrainingalliance.locations.entity;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.electricaltrainingalliance.validations.ValidEntity;

@Entity
@Table(name = "locations", indexes = { 
    @Index(name = "ix_locations_building", columnList = "building_name", unique = false),
    @Index(name = "uk_unique_location", columnList = "building_name, room", unique = true)
})
@NamedQueries({
    @NamedQuery(name = SessionLocation.findByBuilding, query = "SELECT l FROM SessionLocation l WHERE l.buildingName = :building ORDER BY l.room"),
    @NamedQuery(name = SessionLocation.getAll, query = "SELECT l FROM SessionLocation l ORDER BY l.buildingName, l.room"),
})
public class SessionLocation implements Serializable, ValidEntity {

	private static final long serialVersionUID = 1L;

    private static final String PREFIX = "locations.entity.";
    public static final String findByBuilding = PREFIX + "findByBuilding";
    public static final String getAll = PREFIX + "getAll";

    @Id
    @Column(name = "location_id", nullable = false)
    protected UUID locationId;

    @Column(name = "building_name", nullable = false)
    private String buildingName;

    @Column(name = "room", nullable = false)
    private String room;

	public UUID getLocationId() {
        return locationId;
    }

    public void setLocationId(UUID locationId) {
        this.locationId = locationId;
    }

    public String getBuildingName() {
        return buildingName;
    }

    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    @Override
    public int hashCode() {
		return Objects.hash(locationId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SessionLocation other = (SessionLocation) obj;
        if (locationId == null) {
            if (other.locationId != null)
                return false;
        } else if (!locationId.equals(other.locationId))
            return false;
        return true;
    }

    @Override
	@Transient
	@JsonbTransient
	public boolean isValid() {
		// Implement Entity business rules for Validity Checks
        // building is required
        if (this.buildingName == null || this.buildingName.isEmpty() || this.buildingName.isBlank()) return false;
        // room is required
        if (this.room == null || this.room.isEmpty() || this.room.isBlank()) return false;
        // all tests passed
		return true;
	}
    
}
