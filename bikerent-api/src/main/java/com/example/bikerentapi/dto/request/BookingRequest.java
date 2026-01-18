package com.example.bikerentapi.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public record BookingRequest(
        @NotNull
        UUID customerId,

        @NotNull
        UUID bicycleId,

        @NotNull
        @Future
        LocalDateTime plannedStartTime,

        @NotNull
        @Future
        LocalDateTime plannedReturnTime
) {}
