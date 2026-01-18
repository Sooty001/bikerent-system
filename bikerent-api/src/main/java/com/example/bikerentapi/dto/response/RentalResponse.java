package com.example.bikerentapi.dto.response;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Relation(collectionRelation = "rentals", itemRelation = "rental")
public class RentalResponse extends RepresentationModel<RentalResponse> {
    private UUID id;
    private UUID customerId;
    private UUID bicycleId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime expectedReturnTime;
    private double totalCost;
    private String status;

    public RentalResponse() {
    }

    public RentalResponse(UUID id, UUID customerId, UUID bicycleId, LocalDateTime startTime, LocalDateTime endTime, LocalDateTime expectedReturnTime, double totalCost, String status) {
        this.id = id;
        this.customerId = customerId;
        this.bicycleId = bicycleId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.expectedReturnTime = expectedReturnTime;
        this.totalCost = totalCost;
        this.status = status;
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
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        if (!super.equals(object)) return false;
        RentalResponse that = (RentalResponse) object;
        return Double.compare(totalCost, that.totalCost) == 0 && Objects.equals(id, that.id) && Objects.equals(customerId, that.customerId) && Objects.equals(bicycleId, that.bicycleId) && Objects.equals(startTime, that.startTime) && Objects.equals(endTime, that.endTime) && Objects.equals(expectedReturnTime, that.expectedReturnTime) && Objects.equals(status, that.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id, customerId, bicycleId, startTime, endTime, expectedReturnTime, totalCost, status);
    }
}
