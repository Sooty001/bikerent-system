package org.example;

import java.io.Serializable;

public record RentalRatedEvent(
        Long rentalId,
        Long customerId,
        String level,
        Integer score
) implements Serializable {}