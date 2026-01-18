package com.example.bikerentrest.entities;

import com.example.bikerentrest.entities.enums.BicycleStatus;
import jakarta.persistence.*;

@Entity
@Table(name = "bicycles")
public class Bicycle extends BaseEntity {

    private String modelName;
    private String type;
    private String size;
    private BicycleStatus status;
    private Double pricePerHour;
    private String description;
    private String photoUrl;
    private boolean deleted = false;

    protected Bicycle() {}

    public Bicycle(String modelName, String type, String size, Double pricePerHour, String description, String photoUrl) {
        this.modelName = modelName;
        this.type = type;
        this.size = size;
        this.pricePerHour = pricePerHour;
        this.description = description;
        this.photoUrl = photoUrl;
        this.status = BicycleStatus.AVAILABLE;
        this.deleted = false;
    }

    @Column(name = "model_name", nullable = false)
    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    @Column(name = "type", nullable = false)
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Column(name = "size")
    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    public BicycleStatus getStatus() {
        return status;
    }

    public void setStatus(BicycleStatus status) {
        this.status = status;
    }

    @Column(name = "price_per_hour", nullable = false)
    public Double getPricePerHour() {
        return pricePerHour;
    }

    public void setPricePerHour(Double pricePerHour) {
        this.pricePerHour = pricePerHour;
    }

    @Column(name = "description", length = 1000)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(name = "photo_url")
    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    @Column(name = "is_deleted", nullable = false)
    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}