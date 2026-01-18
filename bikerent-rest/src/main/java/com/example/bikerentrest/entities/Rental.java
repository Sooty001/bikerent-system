package com.example.bikerentrest.entities;

import com.example.bikerentrest.entities.enums.RentalStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "rentals")
public class Rental extends BaseEntity {

    private Customer customer;
    private Bicycle bicycle;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime expectedReturnTime;
    private double totalCost;
    private RentalStatus status;

    protected Rental() {}

    public Rental(Customer customer, Bicycle bicycle, LocalDateTime expectedReturnTime) {
        this.customer = customer;
        this.bicycle = bicycle;
        this.startTime = LocalDateTime.now();
        this.expectedReturnTime = expectedReturnTime;
        this.status = RentalStatus.ACTIVE;
        this.totalCost = 0.0;
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

    @Column(name = "start_time", nullable = false)
    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    @Column(name = "end_time")
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Column(name = "expected_return_time", nullable = false)
    public LocalDateTime getExpectedReturnTime() {
        return expectedReturnTime;
    }

    public void setExpectedReturnTime(LocalDateTime expectedReturnTime) {
        this.expectedReturnTime = expectedReturnTime;
    }

    @Column(name = "total_cost")
    public double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    public RentalStatus getStatus() {
        return status;
    }

    public void setStatus(RentalStatus status) {
        this.status = status;
    }
}