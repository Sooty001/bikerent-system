package com.example.bikerentcontracts.events;

import java.io.Serializable;

public record RentalEndedEvent(
        String rentalId,
        String customerId,
        String bicycleId,
        Double finalPrice,
        String currency
) implements Serializable {}