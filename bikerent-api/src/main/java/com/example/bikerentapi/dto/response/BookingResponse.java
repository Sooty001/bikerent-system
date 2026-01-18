package com.example.bikerentapi.dto.response;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Relation(collectionRelation = "bookings", itemRelation = "booking")
public class BookingResponse extends RepresentationModel<BookingResponse>{
    private UUID id;
    private UUID customerId;
    private UUID bicycleId;
    private LocalDateTime plannedStartTime;
    private LocalDateTime plannedReturnTime;
    private String status;
    private LocalDateTime createdAt;

    public BookingResponse() {
    }

    public BookingResponse(UUID id, UUID customerId, UUID bicycleId, LocalDateTime plannedStartTime, LocalDateTime plannedReturnTime, String status, LocalDateTime createdAt) {
        this.id = id;
        this.customerId = customerId;
        this.bicycleId = bicycleId;
        this.plannedStartTime = plannedStartTime;
        this.plannedReturnTime = plannedReturnTime;
        this.status = status;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public UUID getBicycleId() {
        return bicycleId;
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
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        if (!super.equals(object)) return false;
        BookingResponse that = (BookingResponse) object;
        return Objects.equals(id, that.id) && Objects.equals(customerId, that.customerId) && Objects.equals(bicycleId, that.bicycleId) && Objects.equals(plannedStartTime, that.plannedStartTime) && Objects.equals(plannedReturnTime, that.plannedReturnTime) && Objects.equals(status, that.status) && Objects.equals(createdAt, that.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id, customerId, bicycleId, plannedStartTime, plannedReturnTime, status, createdAt);
    }
}
