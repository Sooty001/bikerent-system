package com.example.bikerentcontracts.events;

import java.io.Serializable;

public record BicycleDeletedEvent(
        String bicycleId,
        String modelName,
        Double pricePerHour
) implements Serializable {}