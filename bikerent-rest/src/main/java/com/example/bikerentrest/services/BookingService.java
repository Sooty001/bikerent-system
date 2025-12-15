package com.example.bikerentrest.services;

import com.example.bikerentapi.dto.request.BookingRequest;
import com.example.bikerentapi.dto.response.BookingResponse;
import com.example.bikerentapi.exception.ResourceNotFoundException;
import com.example.bikerentrest.entities.Bicycle;
import com.example.bikerentrest.entities.Booking;
import com.example.bikerentrest.entities.Customer;
import com.example.bikerentrest.entities.enums.BookingStatus;
import com.example.bikerentrest.repositories.BookingRepository;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class BookingService {
    // ... поля и конструктор ...
    private final BookingRepository bookingRepository;
    private final CustomerService customerService;
    private final BicycleService bicycleService;
    private final ModelMapper modelMapper;

    public BookingService(BookingRepository bookingRepository, CustomerService customerService, @Lazy BicycleService bicycleService, ModelMapper modelMapper) {
        this.bookingRepository = bookingRepository;
        this.customerService = customerService;
        this.bicycleService = bicycleService;
        this.modelMapper = modelMapper;
    }

    public BookingResponse createBooking(BookingRequest request) {
        Customer customer = customerService.findEntityById(request.customerId());
        Bicycle bicycle = bicycleService.findEntityById(request.bicycleId());

        boolean isConflict = bookingRepository.existsBookingConflict(
                request.bicycleId(), request.plannedStartTime(), request.plannedReturnTime()
        );

        if (isConflict) {
            throw new IllegalStateException("Велосипед уже забронирован на это время");
        }

        // ИСПОЛЬЗУЕМ КОНСТРУКТОР
        Booking booking = new Booking(
                customer,
                bicycle,
                request.plannedStartTime(),
                request.plannedReturnTime()
        );

        Booking saved = bookingRepository.save(booking);
        return modelMapper.map(saved, BookingResponse.class);
    }

    public BookingResponse cancelBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Бронирование", id));

        booking.setStatus(BookingStatus.CANCELLED); // ENUM
        return modelMapper.map(bookingRepository.save(booking), BookingResponse.class);
    }

    // ... findById, findEntityById, findAll (без изменений, кроме использования modelMapper) ...
    @Transactional(readOnly = true)
    public BookingResponse findById(Long id) {
        return bookingRepository.findById(id)
                .map(b -> modelMapper.map(b, BookingResponse.class))
                .orElseThrow(() -> new ResourceNotFoundException("Бронирование", id));
    }

    public Booking findEntityById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Бронирование", id));
    }

    public List<BookingResponse> findAll() {
        return bookingRepository.findAll().stream()
                .map(b -> modelMapper.map(b, BookingResponse.class))
                .toList();
    }

    public void updateBookingStatus(Long id, String newStatus) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Бронирование", id));

        try {
            booking.setStatus(BookingStatus.valueOf(newStatus.toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("Неверный статус бронирования");
        }
        bookingRepository.save(booking);
    }
}