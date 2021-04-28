package com.pedrorenzo.booking.entities;

import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.Instant;

@Document(collection = "bookings")
@Entity
public class Booking {

    //
    // For the sake of simplicity, I considered that there is no need for any data from the
    // customer who made the booking and that the booking id is enough for any type of consultation needed.
    //

    @Id
    @GeneratedValue
    private String id;
    private Instant fromDate;
    private Instant toDate;
    private Instant createdDate;
    private Instant updatedDate;

    public Booking() {

    }

    public Booking(final Instant fromDate, final Instant toDate,
                   final Instant createdDate, final Instant updatedDate) {
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public Instant getFromDate() {
        return fromDate;
    }

    public void setFromDate(Instant fromDate) {
        this.fromDate = fromDate;
    }

    public Instant getToDate() {
        return toDate;
    }

    public void setToDate(final Instant toDate) {
        this.toDate = toDate;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(final Instant createdDate) {
        this.createdDate = createdDate;
    }

    public Instant getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(final Instant updatedDate) {
        this.updatedDate = updatedDate;
    }
}
