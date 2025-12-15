package com.example.bikerentrest.entities;

import com.example.bikerentrest.entities.enums.BookingStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
public class Booking extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bicycle_id")
    private Bicycle bicycle;

    private LocalDateTime plannedStartTime;
    private LocalDateTime plannedReturnTime;

    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    private LocalDateTime createdAt;

    protected Booking() {}

    public Booking(Customer customer, Bicycle bicycle, LocalDateTime plannedStartTime, LocalDateTime plannedReturnTime) {
        this.customer = customer;
        this.bicycle = bicycle;
        this.plannedStartTime = plannedStartTime;
        this.plannedReturnTime = plannedReturnTime;
        this.status = BookingStatus.CONFIRMED;
        this.createdAt = LocalDateTime.now();
    }


    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }

    public Bicycle getBicycle() { return bicycle; }
    public void setBicycle(Bicycle bicycle) { this.bicycle = bicycle; }

    public LocalDateTime getPlannedStartTime() { return plannedStartTime; }
    public void setPlannedStartTime(LocalDateTime plannedStartTime) { this.plannedStartTime = plannedStartTime; }

    public LocalDateTime getPlannedReturnTime() { return plannedReturnTime; }
    public void setPlannedReturnTime(LocalDateTime plannedReturnTime) { this.plannedReturnTime = plannedReturnTime; }

    public BookingStatus getStatus() { return status; }
    public void setStatus(BookingStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}