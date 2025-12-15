package com.example.bikerentrest.services;

import com.example.bikerentapi.dto.request.BicycleRequest;
import com.example.bikerentapi.dto.response.BicycleResponse;
import com.example.bikerentapi.dto.response.PagedResponse;
import com.example.bikerentapi.exception.ResourceNotFoundException;
import com.example.bikerentrest.config.RabbitMQConfig;
import com.example.bikerentrest.entities.Bicycle;
import com.example.bikerentrest.entities.enums.BicycleStatus;
import com.example.bikerentrest.repositories.BicycleRepository;
import org.example.BicycleDeletedEvent;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class BicycleService {

    private final BicycleRepository bicycleRepository;
    private final RabbitTemplate rabbitTemplate;
    private final ModelMapper modelMapper;

    public BicycleService(BicycleRepository bicycleRepository, RabbitTemplate rabbitTemplate, ModelMapper modelMapper) {
        this.bicycleRepository = bicycleRepository;
        this.rabbitTemplate = rabbitTemplate;
        this.modelMapper = modelMapper;
    }

    public BicycleResponse addBicycle(BicycleRequest request) {
        Bicycle bicycle = modelMapper.map(request, Bicycle.class);
        bicycle.setStatus(BicycleStatus.AVAILABLE); // Используем ENUM

        Bicycle saved = bicycleRepository.save(bicycle);
        return modelMapper.map(saved, BicycleResponse.class);
    }

    @Transactional(readOnly = true)
    public BicycleResponse findById(Long id) {
        // ... (код поиска не изменился)
        Bicycle bicycle = bicycleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Велосипед", id));
        if (bicycle.isDeleted()) throw new ResourceNotFoundException("Велосипед (удален)", id);
        return modelMapper.map(bicycle, BicycleResponse.class);
    }

    public Bicycle findEntityById(Long id) {
        return bicycleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Велосипед", id));
    }

    @Transactional(readOnly = true)
    public PagedResponse<BicycleResponse> findAll(String status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Bicycle> bicyclePage;

        if (status != null && !status.isBlank()) {
            // Преобразуем входящую строку (из URL) в Enum
            // toUpperCase() нужен, так как константы обычно капсом, а в URL может прийти "available"
            try {
                BicycleStatus statusEnum = BicycleStatus.valueOf(status.toUpperCase());
                bicyclePage = bicycleRepository.findByStatusAndDeletedFalse(statusEnum, pageable);
            } catch (IllegalArgumentException e) {
                // Если передали кривой статус, возвращаем пустой список или все (тут решим вернуть все)
                bicyclePage = bicycleRepository.findByDeletedFalse(pageable);
            }
        } else {
            bicyclePage = bicycleRepository.findByDeletedFalse(pageable);
        }

        return new PagedResponse<>(
                bicyclePage.getContent().stream()
                        .map(b -> modelMapper.map(b, BicycleResponse.class))
                        .toList(),
                bicyclePage.getNumber(),
                bicyclePage.getSize(),
                bicyclePage.getTotalElements(),
                bicyclePage.getTotalPages(),
                bicyclePage.isLast()
        );
    }

    public void updateBicycleStatus(Long id, String newStatus) {
        Bicycle bicycle = bicycleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Велосипед", id));

        // Преобразуем String в Enum
        try {
            bicycle.setStatus(BicycleStatus.valueOf(newStatus.toUpperCase()));
            bicycleRepository.save(bicycle);
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("Неверный статус велосипеда: " + newStatus);
        }
    }

    // Перегрузка метода для внутреннего использования с Enum
    public void updateBicycleStatus(Long id, BicycleStatus newStatus) {
        Bicycle bicycle = bicycleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Велосипед", id));
        bicycle.setStatus(newStatus);
        bicycleRepository.save(bicycle);
    }

    public void deleteById(Long id) {
        Bicycle bicycle = bicycleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Велосипед", id));
        bicycle.setDeleted(true);
        bicycleRepository.save(bicycle);

        BicycleDeletedEvent event = new BicycleDeletedEvent(id);
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, RabbitMQConfig.ROUTING_KEY_BICYCLE_DELETED, event);
    }
}