package com.example.bikerentcontracts.events;

import java.io.Serializable;

public record CustomerRegisteredEvent(
        String customerId,
        String fullName,
        String email,
        Integer initialLoyaltyPoints
) implements Serializable {}