package com.example.bikerentapi.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CustomerRequest(
        @NotBlank
        String firstName,

        @NotBlank
        String lastName,

        String patronymic,

        @NotBlank @Size(min = 10)
        String phoneNumber,

        @Email String email
) {}
