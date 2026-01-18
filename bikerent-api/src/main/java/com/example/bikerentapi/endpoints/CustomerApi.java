package com.example.bikerentapi.endpoints;

import com.example.bikerentapi.dto.request.CustomerRequest;
import com.example.bikerentapi.dto.response.CustomerResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "customers", description = "API для управления базой клиентов")
@RequestMapping("/api/customers")
public interface CustomerApi {

    @Operation(summary = "Получить список всех клиентов")
    @ApiResponse(responseCode = "200", description = "Список клиентов получен")
    @GetMapping
    CollectionModel<EntityModel<CustomerResponse>> getAllCustomers();

    @Operation(summary = "Зарегистрировать нового клиента")
    @ApiResponse(responseCode = "201", description = "Клиент успешно зарегистрирован")
    @PostMapping
    ResponseEntity<EntityModel<CustomerResponse>> registerCustomer(@Valid @RequestBody CustomerRequest request);

    @Operation(summary = "Получить информацию о клиенте по ID")
    @ApiResponse(responseCode = "200", description = "Клиент найден")
    @GetMapping("/{id}")
    EntityModel<CustomerResponse> getCustomerById(@PathVariable UUID id);

    @Operation(summary = "Удалить клиента")
    @ApiResponse(responseCode = "204", description = "Клиент успешно помечен как удаленный")
    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteCustomer(@PathVariable UUID id);
}