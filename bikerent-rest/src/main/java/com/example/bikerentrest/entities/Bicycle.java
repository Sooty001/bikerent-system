package com.example.bikerentrest.entities;

import com.example.bikerentrest.entities.enums.BicycleStatus;
import jakarta.persistence.*;

@Entity
@Table(name = "bicycles")
public class Bicycle extends BaseEntity {

    private String modelName;
    private String type;
    private String size;

    @Enumerated(EnumType.STRING) // Сохраняем как строку: "AVAILABLE"
    private BicycleStatus status;

    private Double pricePerHour;

    @Column(length = 1000)
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

    public String getModelName() { return modelName; }
    public void setModelName(String modelName) { this.modelName = modelName; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getSize() { return size; }
    public void setSize(String size) { this.size = size; }

    // Важно: тип теперь BicycleStatus
    public BicycleStatus getStatus() { return status; }
    public void setStatus(BicycleStatus status) { this.status = status; }

    public Double getPricePerHour() { return pricePerHour; }
    public void setPricePerHour(Double pricePerHour) { this.pricePerHour = pricePerHour; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }

    public boolean isDeleted() { return deleted; }
    public void setDeleted(boolean deleted) { this.deleted = deleted; }
}