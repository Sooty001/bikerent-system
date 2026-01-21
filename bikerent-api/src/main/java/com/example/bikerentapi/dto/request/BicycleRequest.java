package com.example.bikerentapi.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record BicycleRequest(
        @NotBlank(message = "Название модели не может быть пустым")
        String modelName,

        @NotBlank(message = "Тип не может быть пустым")
        String type,

        @NotBlank(message = "Размер не может быть пустым")
        String size,

        @NotNull(message = "Стоимость за час не может быть пустой")
        @Positive(message = "Стоимость за час должна быть положительной")
        double pricePerHour,

        String description,
        String photoUrl
) {}