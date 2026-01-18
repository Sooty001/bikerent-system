package com.example.bikerentapi.endpoints;

import com.example.bikerentapi.dto.request.RentalRequest;
import com.example.bikerentapi.dto.response.RentalResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "rentals", description = "API для управления текущими арендами")
@RequestMapping("/api/rentals")
public interface RentalApi {

    @Operation(summary = "Получить информацию об аренде по ID")
    @ApiResponse(responseCode = "200", description = "Аренда найдена")
    @GetMapping("/{id}")
    EntityModel<RentalResponse> getRentalById(@PathVariable UUID id);

    @Operation(summary = "Начать аренду для клиента (без брони)")
    @PostMapping("/walk-in")
    ResponseEntity<EntityModel<RentalResponse>> startWalkInRental(@Valid @RequestBody RentalRequest request);

    @Operation(summary = "Начать аренду на основе существующего бронирования")
    @PostMapping("/from-booking/{bookingId}")
    ResponseEntity<EntityModel<RentalResponse>> startRentalFromBooking(@PathVariable UUID bookingId);

    @Operation(summary = "Завершить аренду и рассчитать стоимость")
    @PostMapping("/{id}/complete")
    EntityModel<RentalResponse> completeRental(@PathVariable UUID id);
}