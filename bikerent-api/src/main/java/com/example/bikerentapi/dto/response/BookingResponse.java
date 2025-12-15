package com.example.bikerentapi.dto.response;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.time.LocalDateTime;
import java.util.Objects;

@Relation(collectionRelation = "bookings", itemRelation = "booking")
public class BookingResponse extends RepresentationModel<BookingResponse>{
    private Long id;
    private CustomerResponse customer;
    private BicycleResponse bicycle;
    private LocalDateTime plannedStartTime;
    private LocalDateTime plannedReturnTime;
    private String status;
    private LocalDateTime createdAt;

    public BookingResponse() {
    }

    public BookingResponse(Long id, CustomerResponse customer, BicycleResponse bicycle, LocalDateTime plannedStartTime, LocalDateTime plannedReturnTime, String status, LocalDateTime createdAt) {
        this.id = id;
        this.customer = customer;
        this.bicycle = bicycle;
        this.plannedStartTime = plannedStartTime;
        this.plannedReturnTime = plannedReturnTime;
        this.status = status;
        this.createdAt = createdAt;
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

    public LocalDateTime getPlannedStartTime() {
        return plannedStartTime;
    }

    public LocalDateTime getPlannedReturnTime() {
        return plannedReturnTime;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        BookingResponse that = (BookingResponse) o;
        return Objects.equals(id, that.id) && Objects.equals(customer, that.customer) && Objects.equals(bicycle, that.bicycle) && Objects.equals(plannedStartTime, that.plannedStartTime) && Objects.equals(plannedReturnTime, that.plannedReturnTime) && Objects.equals(status, that.status) && Objects.equals(createdAt, that.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id, customer, bicycle, plannedStartTime, plannedReturnTime, status, createdAt);
    }
}
