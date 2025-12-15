package org.example;

import java.io.Serializable;

public record BicycleDeletedEvent(
        Long bicycleId
) implements Serializable {}