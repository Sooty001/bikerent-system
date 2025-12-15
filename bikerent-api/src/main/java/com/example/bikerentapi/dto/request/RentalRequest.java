package com.example.bikerentapi.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record RentalRequest(
        @NotNull
        Long customerId,

        @NotNull
        Long bicycleId,

        @NotNull
        @Future
        LocalDateTime expectedReturnTime
) {}
