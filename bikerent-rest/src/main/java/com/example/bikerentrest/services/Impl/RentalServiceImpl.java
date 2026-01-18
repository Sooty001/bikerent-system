package com.example.bikerentrest.services.Impl;

import com.example.bikerentapi.dto.request.RentalRequest;
import com.example.bikerentapi.dto.response.RentalResponse;
import com.example.bikerentapi.exception.bicycle.BicycleNotAvailableException;
import com.example.bikerentapi.exception.booking.BookingNotConfirmedException;
import com.example.bikerentapi.exception.rental.RentalAlreadyCompletedException;
import com.example.bikerentapi.exception.rental.RentalNotFoundException;
import com.example.bikerentcontracts.events.RentalEndedEvent;
import com.example.bikerentcontracts.events.RentalStartedEvent;
import com.example.bikerentrest.services.BicycleService;
import com.example.bikerentrest.services.BookingService;
import com.example.bikerentrest.services.CustomerService;
import com.example.bikerentrest.services.RentalService;
import com.example.bikerentrest.services.grpc.PricingGrpcClient;
import com.example.bikerentrest.config.RabbitMQConfig;
import com.example.bikerentrest.entities.*;
import com.example.bikerentrest.entities.enums.BicycleStatus;
import com.example.bikerentrest.entities.enums.BookingStatus;
import com.example.bikerentrest.entities.enums.RentalStatus;
import com.example.bikerentrest.repositories.RentalRepository;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service

public class RentalServiceImpl implements RentalService {
    private static final Logger log = LoggerFactory.getLogger(RentalService.class);

    private final RentalRepository rentalRepository;
    private final CustomerService customerService;
    private final BicycleService bicycleService;
    private final BookingService bookingService;
    private final RabbitTemplate rabbitTemplate;
    private final ModelMapper modelMapper;

    private final PricingGrpcClient pricingGrpcClient;

    public RentalServiceImpl(RentalRepository rentalRepository,
                         CustomerService customerService,
                         BicycleServiceImpl bicycleService,
                         BookingService bookingService,
                         RabbitTemplate rabbitTemplate,
                         ModelMapper modelMapper,
                         PricingGrpcClient pricingGrpcClient) {
        this.rentalRepository = rentalRepository;
        this.customerService = customerService;
        this.bicycleService = bicycleService;
        this.bookingService = bookingService;
        this.rabbitTemplate = rabbitTemplate;
        this.modelMapper = modelMapper;
        this.pricingGrpcClient = pricingGrpcClient;
    }

    @Override
    public RentalResponse findById(UUID id) {
        return rentalRepository.findById(id)
                .map(r -> modelMapper.map(r, RentalResponse.class))
                .orElseThrow(() -> new RentalNotFoundException(id));
    }

    @Override
    @Transactional
    public RentalResponse startWalkInRental(RentalRequest request) {
        Customer customer = customerService.findEntityById(request.customerId());
        Bicycle bicycle = bicycleService.findEntityById(request.bicycleId());

        if (bicycle.getStatus() != BicycleStatus.AVAILABLE) {
            throw new BicycleNotAvailableException(bicycle.getId());
        }

        Rental rental = new Rental(
                customer,
                bicycle,
                request.expectedReturnTime()
        );

        Rental saved = rentalRepository.save(rental);
        bicycleService.updateBicycleStatus(bicycle.getId(), BicycleStatus.RENTED);

        sendStartEvent(saved);

        log.info("Started walk-in rental with ID: {}", saved.getId());
        return modelMapper.map(saved, RentalResponse.class);
    }

    @Override
    @Transactional
    public RentalResponse startRentalFromBooking(UUID bookingId) {
        Booking booking = bookingService.findEntityById(bookingId);

        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new BookingNotConfirmedException(bookingId, booking.getStatus().toString());
        }
        if (booking.getBicycle().getStatus() != BicycleStatus.AVAILABLE) {
            throw new BicycleNotAvailableException(booking.getBicycle().getId());
        }

        Rental rental = new Rental(
                booking.getCustomer(),
                booking.getBicycle(),
                booking.getPlannedReturnTime()
        );

        Rental saved = rentalRepository.save(rental);

        bicycleService.updateBicycleStatus(booking.getBicycle().getId(), BicycleStatus.RENTED);
        bookingService.updateBookingStatus(bookingId, BookingStatus.COMPLETED.name());

        sendStartEvent(saved);

        log.info("Started rental from booking. Rental ID: {}", saved.getId());
        return modelMapper.map(saved, RentalResponse.class);
    }

    @Override
    @Transactional
    public RentalResponse completeRental(UUID id) {
        Rental rental = rentalRepository.findById(id)
                .orElseThrow(() -> new RentalNotFoundException(id));

        if (rental.getStatus() != RentalStatus.ACTIVE) {
            throw new RentalAlreadyCompletedException(id);
        }

        LocalDateTime endTime = LocalDateTime.now();
        rental.setEndTime(endTime);

        double finalPrice = pricingGrpcClient.calculatePrice(
                rental.getBicycle().getId(),
                rental.getCustomer().getId(),
                rental.getBicycle().getPricePerHour(),
                rental.getCustomer().getLoyaltyPoints() == null ? 0 : rental.getCustomer().getLoyaltyPoints(),
                rental.getStartTime(),
                endTime
        );

        int pointsDelta = 5;

        if (endTime.isAfter(rental.getExpectedReturnTime().plusMinutes(15))) {
            pointsDelta -= 10;
            log.info("Penalty applied: Late return for rental {}", id);
        }

        if (finalPrice > 3000.0) {
            pointsDelta += 20;
            log.info("Bonus applied: High spender for rental {}", id);
        }

        customerService.updateLoyaltyPoints(rental.getCustomer().getId(), pointsDelta);

        rental.setTotalCost(finalPrice);
        rental.setStatus(RentalStatus.COMPLETED);

        Rental saved = rentalRepository.save(rental);
        bicycleService.updateBicycleStatus(rental.getBicycle().getId(), BicycleStatus.AVAILABLE);

        try {
            RentalEndedEvent event = new RentalEndedEvent(
                    saved.getId().toString(),
                    saved.getCustomer().getId().toString(),
                    saved.getBicycle().getId().toString(),
                    saved.getTotalCost(),
                    "RUB"
            );

            rabbitTemplate.convertAndSend(RabbitMQConfig.FANOUT_EXCHANGE, "", event);

            log.info("Sent RentalEndedEvent (Fanout) for rental {}", saved.getId());
        } catch (Exception e) {
            log.error("Failed to publish RentalEndedEvent", e);
        }

        return modelMapper.map(saved, RentalResponse.class);
    }

    private void sendStartEvent(Rental rental) {
        try {
            RentalStartedEvent event = new RentalStartedEvent(
                    rental.getId().toString(),
                    rental.getCustomer().getId().toString(),
                    rental.getBicycle().getId().toString(),
                    rental.getStartTime().toString()
            );
            rabbitTemplate.convertAndSend(RabbitMQConfig.TOPIC_EXCHANGE, RabbitMQConfig.KEY_RENTAL_STARTED, event);
        } catch (Exception e) {
            log.error("Failed to send RentalStartedEvent", e);
        }
    }
}