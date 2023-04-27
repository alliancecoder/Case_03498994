package org.electricaltrainingalliance.sessions.slots.entity;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.UUID;

import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.electricaltrainingalliance.validations.ValidEntity;

import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Table(name = "time_slots")
@NamedQuery(name = TimeSlot.getAll, query = "SELECT s FROM TimeSlot s ORDER BY s.slotDate, s.slotTime")
public class TimeSlot implements Serializable, Comparable<TimeSlot>, ValidEntity {

	private static final long serialVersionUID = 1L;

    private static final String PREFIX = "sessions.slots.entity.";
    public static final String getAll = PREFIX + "getAll";

    @Id
    @Column(name = "slot_id", nullable = false)
    protected UUID slotId;

    @Enumerated(EnumType.STRING)
    @Column(name = "slot_day", nullable = false)
    private Days day;

    @Column(name = "slot_date", nullable = false)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date slotDate;

    @Column(name = "slot_time", nullable = false)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    private Time slotTime;

    public UUID getSlotId() {
        return slotId;
    }

    public void setSlotId(UUID slotId) {
        this.slotId = slotId;
    }

    public Days getDay() {
        return day;
    }

    public void setDay(Days day) {
        this.day = day;
    }

    public String getSlotDate() {
        return slotDate.toString();
    }

    public void setSlotDate(String slotDate) {
        try {
            java.util.Date jDate = new SimpleDateFormat("yyyy-MM-dd").parse(slotDate);
            this.slotDate = new Date(jDate.getTime());            
        } catch (Exception e) {
            this.slotDate = null;
        }
    }

    public String getSlotTime() {
        DateFormat format = new SimpleDateFormat( "h:mm a" );
        return format.format(slotTime);
    }

    public void setSlotTime(String slotTime) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat( "h:mm a" );
            long ms = sdf.parse(slotTime).getTime();
            this.slotTime = new Time(ms);         
        } catch (Exception e) {
            this.slotTime = null;
        }
    }

    @Override
    public int compareTo(TimeSlot other) {
        return Comparator.comparing(TimeSlot::getSlotDate)
            .thenComparing(TimeSlot::getSlotTime)
            .compare(this, other);
    }

    @Override
	@Transient
	@JsonbTransient
	public boolean isValid() {
		// Implement Entity business rules for Validity Checks
        // days is required
        if (this.day == null) return false;
        // date is required
        if (this.slotDate == null) return false;
        // time is required
        if (this.slotTime == null) return false;
        // all tests passed
		return true;
    }
    
}
