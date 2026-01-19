package com.example.bikerentcontracts.events;

import java.io.Serializable;

public record RentalStartedEvent(
        String rentalId,
        String customerId,
        String bicycleId,
        String startTime
) implements Serializable {}