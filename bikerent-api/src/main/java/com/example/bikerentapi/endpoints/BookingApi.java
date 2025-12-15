package com.example.bikerentapi.endpoints;

import com.example.bikerentapi.dto.request.BookingRequest;
import com.example.bikerentapi.dto.response.BookingResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "bookings", description = "API для управления бронированиями")
@RequestMapping("/api/bookings")
public interface BookingApi {

    @Operation(summary = "Получить список всех бронирований")
    @ApiResponse(responseCode = "200", description = "Список бронирований получен")
    @GetMapping
    CollectionModel<EntityModel<BookingResponse>> getAllBookings();

    @Operation(summary = "Получить информацию о бронировании по ID")
    @ApiResponse(responseCode = "200", description = "Бронирование найдено")
    @ApiResponse(responseCode = "404", description = "Бронирование не найдено")
    @GetMapping("/{id}")
    EntityModel<BookingResponse> getBookingById(@PathVariable Long id);

    @Operation(summary = "Создать новое бронирование")
    @ApiResponse(responseCode = "201", description = "Бронь успешно создана")
    @ApiResponse(responseCode = "409", description = "Велосипед уже занят на это время")
    @PostMapping
    ResponseEntity<EntityModel<BookingResponse>> createBooking(@Valid @RequestBody BookingRequest request);

    @Operation(summary = "Отменить бронирование")
    @ApiResponse(responseCode = "200", description = "Бронь успешно отменена")
    @ApiResponse(responseCode = "404", description = "Бронь не найдена")
    @PostMapping("/{id}/cancel")
    EntityModel<BookingResponse> cancelBooking(@PathVariable Long id);
}
