package com.example.bikerentapi.dto.response;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.time.LocalDateTime;
import java.util.Objects;

@Relation(collectionRelation = "rentals", itemRelation = "rental")
public class RentalResponse extends RepresentationModel<RentalResponse> {
    private Long id;
    private CustomerResponse customer;
    private BicycleResponse bicycle;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime expectedReturnTime;
    private double totalCost;
    private String status;

    public RentalResponse() {
    }

    public RentalResponse(Long id, CustomerResponse customer, BicycleResponse bicycle, LocalDateTime startTime, LocalDateTime endTime, LocalDateTime expectedReturnTime, double totalCost, String status) {
        this.id = id;
        this.customer = customer;
        this.bicycle = bicycle;
        this.startTime = startTime;
        this.endTime = endTime;
        this.expectedReturnTime = expectedReturnTime;
        this.totalCost = totalCost;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public CustomerResponse getCustomer() {
        return customer;
    }

    public BicycleResponse getBicycle() {
        return bicycle;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public LocalDateTime getExpectedReturnTime() {
        return expectedReturnTime;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        RentalResponse that = (RentalResponse) o;
        return Double.compare(totalCost, that.totalCost) == 0 && Objects.equals(id, that.id) && Objects.equals(customer, that.customer) && Objects.equals(bicycle, that.bicycle) && Objects.equals(startTime, that.startTime) && Objects.equals(endTime, that.endTime) && Objects.equals(expectedReturnTime, that.expectedReturnTime) && Objects.equals(status, that.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id, customer, bicycle, startTime, endTime, expectedReturnTime, totalCost, status);
    }
}
