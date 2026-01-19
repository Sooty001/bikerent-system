package com.example.bikerentcontracts.events;

import java.io.Serializable;

public record BookingCreatedEvent(
        String bookingId,
        String customerId,
        String bicycleId,
        String plannedStartTime
) implements Serializable {}