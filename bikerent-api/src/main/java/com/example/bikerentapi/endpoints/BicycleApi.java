package com.example.bikerentapi.endpoints;

import com.example.bikerentapi.dto.request.BicycleRequest;
import com.example.bikerentapi.dto.response.BicycleResponse;
import com.example.bikerentapi.dto.response.PagedResponse;
import com.example.bikerentapi.dto.response.StatusResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "bicycles", description = "API для управления парком велосипедов")
@RequestMapping("/api/bicycles")
public interface BicycleApi {

    @Operation(summary = "Получить список всех велосипедов c пагинацией",
            description = "Позволяет фильтровать по статусу и получать данные постранично")
    @ApiResponse(responseCode = "200", description = "Страница с велосипедами получена")
    @GetMapping
    PagedModel<EntityModel<BicycleResponse>> getAllBicycles(
            @Parameter(description = "Фильтр по статусу (available, rented)")
            @RequestParam(required = false) String status,

            @Parameter(description = "Номер страницы")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Размер страницы")
            @RequestParam(defaultValue = "10") int size
    );

    @Operation(summary = "Получить информацию о велосипеде по ID")
    @ApiResponse(responseCode = "200", description = "Велосипед найден")
    @ApiResponse(responseCode = "404", description = "Велосипед не найден")
    @GetMapping("/{id}")
    EntityModel<BicycleResponse> getBicycleById(@PathVariable UUID id);

    @Operation(summary = "Добавить новый велосипед в парк")
    @ApiResponse(responseCode = "201", description = "Велосипед успешно добавлен")
    @ApiResponse(responseCode = "400", description = "Ошибка валидации данных")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    ResponseEntity<EntityModel<BicycleResponse>> addBicycle(@Valid @RequestBody BicycleRequest request);

    @Operation(summary = "Удалить велосипед")
    @ApiResponse(responseCode = "204", description = "Велосипед успешно помечен как удаленный")
    @ApiResponse(responseCode = "404", description = "Велосипед не найден")
    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteBicycle(@PathVariable UUID id);
}
