package com.example.bikerentapi.dto.response;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.util.Objects;
import java.util.UUID;

@Relation(collectionRelation = "bicycles", itemRelation = "bicycle")
public class BicycleResponse extends RepresentationModel<BicycleResponse> {
    private UUID id;
    private String modelName;
    private String type;
    private String size;
    private String status;
    private Double pricePerHour;
    private String description;
    private String photoUrl;
    private Boolean isDeleted;

    public BicycleResponse() {
    }

    public BicycleResponse(UUID id, String modelName, String type, String size, String status, Double pricePerHour, String description, String photoUrl, Boolean isDeleted) {
        this.id = id;
        this.modelName = modelName;
        this.type = type;
        this.size = size;
        this.status = status;
        this.pricePerHour = pricePerHour;
        this.description = description;
        this.photoUrl = photoUrl;
        this.isDeleted = isDeleted;
    }

    public UUID getId() {
        return id;
    }

    public String getModelName() {
        return modelName;
    }

    public String getType() {
        return type;
    }

    public String getSize() {
        return size;
    }

    public String getStatus() {
        return status;
    }

    public Double getPricePerHour() {
        return pricePerHour;
    }

    public String getDescription() {
        return description;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public Boolean getDeleted() {
        return isDeleted;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        BicycleResponse that = (BicycleResponse) o;
        return Objects.equals(id, that.id) && Objects.equals(modelName, that.modelName) && Objects.equals(type, that.type) && Objects.equals(size, that.size) && Objects.equals(status, that.status) && Objects.equals(pricePerHour, that.pricePerHour) && Objects.equals(description, that.description) && Objects.equals(photoUrl, that.photoUrl) && Objects.equals(isDeleted, that.isDeleted);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id, modelName, type, size, status, pricePerHour, description, photoUrl, isDeleted);
    }
}
