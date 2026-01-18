package com.example.bikerentrest.services.Impl;

import com.example.bikerentapi.dto.request.BicycleRequest;
import com.example.bikerentapi.dto.response.BicycleResponse;
import com.example.bikerentapi.dto.response.PagedResponse;
import com.example.bikerentapi.exception.bicycle.BicycleNotFoundException;
import com.example.bikerentapi.exception.bicycle.InvalidBicycleStatusException;
import com.example.bikerentcontracts.events.BicycleDeletedEvent;
import com.example.bikerentrest.config.RabbitMQConfig;
import com.example.bikerentrest.entities.Bicycle;
import com.example.bikerentrest.entities.enums.BicycleStatus;
import com.example.bikerentrest.repositories.BicycleRepository;
import com.example.bikerentrest.services.BicycleService;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class BicycleServiceImpl implements BicycleService {
    private final BicycleRepository bicycleRepository;
    private final RabbitTemplate rabbitTemplate;
    private final ModelMapper modelMapper;

    public BicycleServiceImpl(BicycleRepository bicycleRepository, RabbitTemplate rabbitTemplate, ModelMapper modelMapper) {
        this.bicycleRepository = bicycleRepository;
        this.rabbitTemplate = rabbitTemplate;
        this.modelMapper = modelMapper;
    }

    @Override
    public BicycleResponse addBicycle(BicycleRequest request) {
        Bicycle bicycle = modelMapper.map(request, Bicycle.class);
        bicycle.setStatus(BicycleStatus.AVAILABLE);
        return modelMapper.map(bicycleRepository.save(bicycle), BicycleResponse.class);
    }

    @Override
    public BicycleResponse findById(UUID id) {
        Bicycle bicycle = bicycleRepository.findById(id)
                .orElseThrow(() -> new BicycleNotFoundException(id));

        if (bicycle.isDeleted()) {
            throw new BicycleNotFoundException(id);
        }
        return modelMapper.map(bicycle, BicycleResponse.class);
    }

    @Override
    public Bicycle findEntityById(UUID id) {
        return bicycleRepository.findById(id)
                .orElseThrow(() -> new BicycleNotFoundException(id));
    }

    @Override
    public PagedResponse<BicycleResponse> findAll(String status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Bicycle> bicyclePage;

        if (status != null && !status.isBlank()) {
            try {
                BicycleStatus statusEnum = BicycleStatus.valueOf(status.toUpperCase());
                bicyclePage = bicycleRepository.findByStatusAndDeletedFalse(statusEnum, pageable);
            } catch (IllegalArgumentException e) {
                throw new InvalidBicycleStatusException(status);
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

    @Override
    public void updateBicycleStatus(UUID id, BicycleStatus newStatus) {
        Bicycle bicycle = bicycleRepository.findById(id)
                .orElseThrow(() -> new BicycleNotFoundException(id));
        bicycle.setStatus(newStatus);
        bicycleRepository.save(bicycle);
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        Bicycle bicycle = bicycleRepository.findById(id)
                .orElseThrow(() -> new BicycleNotFoundException(id));

        bicycle.setDeleted(true);
        bicycleRepository.save(bicycle);

        BicycleDeletedEvent event = new BicycleDeletedEvent(
                id.toString(),
                bicycle.getModelName(),
                bicycle.getPricePerHour()
        );
        rabbitTemplate.convertAndSend(RabbitMQConfig.TOPIC_EXCHANGE, RabbitMQConfig.KEY_BICYCLE_DELETED, event);
    }
}