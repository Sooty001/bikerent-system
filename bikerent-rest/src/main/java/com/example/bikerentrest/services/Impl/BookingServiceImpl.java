package com.example.bikerentrest.services.Impl;

import com.example.bikerentapi.dto.request.BookingRequest;
import com.example.bikerentapi.dto.response.BookingResponse;
import com.example.bikerentapi.exception.bicycle.BicycleNotFoundException;
import com.example.bikerentapi.exception.bicycle.BicycleUnderMaintenanceException;
import com.example.bikerentapi.exception.booking.BookingNotFoundException;
import com.example.bikerentapi.exception.booking.BookingTimeConflictException;
import com.example.bikerentapi.exception.booking.InvalidBookingTimeRangeException;
import com.example.bikerentcontracts.events.BookingCreatedEvent;
import com.example.bikerentrest.config.RabbitMQConfig;
import com.example.bikerentrest.entities.Bicycle;
import com.example.bikerentrest.entities.Booking;
import com.example.bikerentrest.entities.Customer;
import com.example.bikerentrest.entities.enums.BicycleStatus;
import com.example.bikerentrest.entities.enums.BookingStatus;
import com.example.bikerentrest.repositories.BookingRepository;
import com.example.bikerentrest.repositories.RentalRepository;
import com.example.bikerentrest.services.BookingService;
import com.example.bikerentrest.services.CustomerService;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class BookingServiceImpl implements BookingService {
    private final RentalRepository rentalRepository;
    private final BookingRepository bookingRepository;
    private final CustomerService customerService;
    private final BicycleServiceImpl bicycleService;
    private final RabbitTemplate rabbitTemplate;
    private final ModelMapper modelMapper;

    public BookingServiceImpl(RentalRepository rentalRepository, BookingRepository bookingRepository, CustomerService customerService, @Lazy BicycleServiceImpl bicycleService, RabbitTemplate rabbitTemplate, ModelMapper modelMapper) {
        this.rentalRepository = rentalRepository;
        this.bookingRepository = bookingRepository;
        this.customerService = customerService;
        this.bicycleService = bicycleService;
        this.rabbitTemplate = rabbitTemplate;
        this.modelMapper = modelMapper;
    }

    @Override
    @Transactional
    public BookingResponse createBooking(BookingRequest request) {
        if (!request.plannedReturnTime().isAfter(request.plannedStartTime())) {
            throw new InvalidBookingTimeRangeException("Rental end time must be after start time");
        }

        if (request.plannedStartTime().plusMinutes(15).isAfter(request.plannedReturnTime())) {
            throw new InvalidBookingTimeRangeException("Minimum booking duration is 15 minutes");
        }

        Customer customer = customerService.findEntityById(request.customerId());
        Bicycle bicycle = bicycleService.findEntityById(request.bicycleId());

        if (bicycle.getStatus() == BicycleStatus.MAINTENANCE) {
            throw new BicycleUnderMaintenanceException(bicycle.getId());
        }
        if (bicycle.isDeleted()) {
            throw new BicycleNotFoundException(bicycle.getId());
        }

        boolean bookingConflict = bookingRepository.existsBookingConflict(
                bicycle.getId(), request.plannedStartTime(), request.plannedReturnTime()
        );

        LocalDateTime safeStartTime = request.plannedStartTime().minusMinutes(30);

        boolean rentalConflict = rentalRepository.existsActiveRentalConflict(
                bicycle.getId(), safeStartTime
        );

        if (bookingConflict || rentalConflict) {
            throw new BookingTimeConflictException(bicycle.getId());
        }

        Booking booking = new Booking(customer, bicycle, request.plannedStartTime(), request.plannedReturnTime());
        Booking saved = bookingRepository.save(booking);

        try {
            BookingCreatedEvent event = new BookingCreatedEvent(
                    saved.getId().toString(),
                    saved.getCustomer().getId().toString(),
                    saved.getBicycle().getId().toString(),
                    saved.getPlannedStartTime().toString()
            );

            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.TOPIC_EXCHANGE,
                    RabbitMQConfig.KEY_BOOKING_CREATED,
                    event
            );
        } catch (Exception e) {
            System.err.println("Failed to send booking event: " + e.getMessage());
        }

        return modelMapper.map(saved, BookingResponse.class);
    }

    @Override
    public BookingResponse cancelBooking(UUID id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException(id));

        booking.setStatus(BookingStatus.CANCELLED);
        return modelMapper.map(bookingRepository.save(booking), BookingResponse.class);
    }

    @Override
    public BookingResponse findById(UUID id) {
        return bookingRepository.findById(id)
                .map(b -> modelMapper.map(b, BookingResponse.class))
                .orElseThrow(() -> new BookingNotFoundException(id));
    }

    @Override
    public Booking findEntityById(UUID id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException(id));
    }

    @Override
    public List<BookingResponse> findAll() {
        return bookingRepository.findAll().stream()
                .map(b -> modelMapper.map(b, BookingResponse.class))
                .toList();
    }

    @Override
    public void updateBookingStatus(UUID id, String newStatus) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException(id));
        try {
            booking.setStatus(BookingStatus.valueOf(newStatus.toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid booking status: " + newStatus);
        }
        bookingRepository.save(booking);
    }
}