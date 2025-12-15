package com.example.bikerentapi.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record BookingRequest(
        @NotNull
        Long customerId,

        @NotNull
        Long bicycleId,

        @NotNull
        @Future
        LocalDateTime plannedStartTime,

        @NotNull
        @Future
        LocalDateTime plannedReturnTime
) {}
