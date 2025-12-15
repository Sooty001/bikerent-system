package com.example.bikerentrest.entities;

import com.example.bikerentrest.entities.enums.RentalStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "rentals")
public class Rental extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bicycle_id")
    private Bicycle bicycle;

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime expectedReturnTime;
    private double totalCost;

    @Enumerated(EnumType.STRING)
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

    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }

    public Bicycle getBicycle() { return bicycle; }
    public void setBicycle(Bicycle bicycle) { this.bicycle = bicycle; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    public LocalDateTime getExpectedReturnTime() { return expectedReturnTime; }
    public void setExpectedReturnTime(LocalDateTime expectedReturnTime) { this.expectedReturnTime = expectedReturnTime; }

    public double getTotalCost() { return totalCost; }
    public void setTotalCost(double totalCost) { this.totalCost = totalCost; }

    public RentalStatus getStatus() { return status; }
    public void setStatus(RentalStatus status) { this.status = status; }
}