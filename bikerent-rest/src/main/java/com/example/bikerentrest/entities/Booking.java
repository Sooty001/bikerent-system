package com.example.bikerentrest.entities;

import com.example.bikerentrest.entities.enums.BookingStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
public class Booking extends BaseEntity {

    private Customer customer;
    private Bicycle bicycle;
    private LocalDateTime plannedStartTime;
    private LocalDateTime plannedReturnTime;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bicycle_id", nullable = false)
    public Bicycle getBicycle() {
        return bicycle;
    }

    public void setBicycle(Bicycle bicycle) {
        this.bicycle = bicycle;
    }

    @Column(name = "planned_start_time", nullable = false)
    public LocalDateTime getPlannedStartTime() {
        return plannedStartTime;
    }

    public void setPlannedStartTime(LocalDateTime plannedStartTime) {
        this.plannedStartTime = plannedStartTime;
    }

    @Column(name = "planned_return_time", nullable = false)
    public LocalDateTime getPlannedReturnTime() {
        return plannedReturnTime;
    }

    public void setPlannedReturnTime(LocalDateTime plannedReturnTime) {
        this.plannedReturnTime = plannedReturnTime;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    @Column(name = "created_at", nullable = false)
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}