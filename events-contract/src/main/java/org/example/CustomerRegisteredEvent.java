package org.example;

import java.io.Serializable;

public record CustomerRegisteredEvent(
        Long customerId,
        String fullName,
        String email
) implements Serializable {}
