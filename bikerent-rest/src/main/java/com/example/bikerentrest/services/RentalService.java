package com.example.bikerentrest.services;

import com.example.bikerentapi.dto.request.RentalRequest;
import com.example.bikerentapi.dto.response.RentalResponse;
import com.example.bikerentapi.exception.ResourceNotFoundException;
import com.example.bikerentrest.config.RabbitMQConfig;
import com.example.bikerentrest.entities.*;
import com.example.bikerentrest.entities.enums.BicycleStatus;
import com.example.bikerentrest.entities.enums.BookingStatus;
import com.example.bikerentrest.entities.enums.RentalStatus;
import com.example.bikerentrest.repositories.RentalRepository;
import com.example.grpc.stats.CalculateRatingRequest;
import com.example.grpc.stats.CalculateRatingResponse;
import com.example.grpc.stats.StatisticsServiceGrpc;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.example.RentalRatedEvent;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
@Transactional
public class RentalService {
    // TODO: все ошибки и логирование всегда везде делается на АНГЛ языке, русский не используем. Это во всех сервисах, везде
    // FIXME: Очень стремный сервис, мне кажется тут слишко много всего, надо будет с этим что то делать(пока забей на это не обращай внимания)
    private final RentalRepository rentalRepository;
    private final CustomerService customerService;
    private final BicycleService bicycleService;
    private final BookingService bookingService;
    private final RabbitTemplate rabbitTemplate;
    private final ModelMapper modelMapper;

    @GrpcClient("stats-service")
    private StatisticsServiceGrpc.StatisticsServiceBlockingStub statsClient;

    public RentalService(RentalRepository rentalRepository, CustomerService customerService, BicycleService bicycleService, @Lazy BookingService bookingService, RabbitTemplate rabbitTemplate, ModelMapper modelMapper) {
        this.rentalRepository = rentalRepository;
        this.customerService = customerService;
        this.bicycleService = bicycleService;
        this.bookingService = bookingService;
        this.rabbitTemplate = rabbitTemplate;
        this.modelMapper = modelMapper;
    }

    @Transactional(readOnly = true)
    public RentalResponse findById(Long id) {
        return rentalRepository.findById(id)
                .map(r -> modelMapper.map(r, RentalResponse.class))
                .orElseThrow(() -> new ResourceNotFoundException("Аренда", id));
    }

    public RentalResponse startWalkInRental(RentalRequest request) {
        Customer customer = customerService.findEntityById(request.customerId());
        Bicycle bicycle = bicycleService.findEntityById(request.bicycleId());

        if (bicycle.getStatus() != BicycleStatus.AVAILABLE) {
            throw new IllegalStateException("Велосипед недоступен");
        }

        Rental rental = new Rental(
                customer,
                bicycle,
                request.expectedReturnTime()
        );

        Rental saved = rentalRepository.save(rental);
        bicycleService.updateBicycleStatus(bicycle.getId(), BicycleStatus.RENTED);

        return modelMapper.map(saved, RentalResponse.class);
    }

    public RentalResponse startRentalFromBooking(Long bookingId) {
        Booking booking = bookingService.findEntityById(bookingId);

        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new IllegalStateException("Бронь не подтверждена");
        }
        if (booking.getBicycle().getStatus() != BicycleStatus.AVAILABLE) {
            throw new IllegalStateException("Велосипед занят");
        }

        Rental rental = new Rental(
                booking.getCustomer(),
                booking.getBicycle(),
                booking.getPlannedReturnTime()
        );

        Rental saved = rentalRepository.save(rental);

        bicycleService.updateBicycleStatus(booking.getBicycle().getId(), BicycleStatus.RENTED);
        bookingService.updateBookingStatus(bookingId, BookingStatus.COMPLETED.name());

        return modelMapper.map(saved, RentalResponse.class);
    }

    public RentalResponse completeRental(Long id) {
        Rental rental = rentalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Аренда", id));

        if (rental.getStatus() != RentalStatus.ACTIVE) {
            throw new IllegalStateException("Аренда уже завершена");
        }

        LocalDateTime endTime = LocalDateTime.now();
        rental.setEndTime(endTime);

        Duration duration = Duration.between(rental.getStartTime(), endTime);
        long minutes = Math.max(1, duration.toMinutes());
        double pricePerHour = rental.getBicycle().getPricePerHour();
        double totalCost = Math.round((pricePerHour / 60.0 * minutes) * 100.0) / 100.0;

        rental.setTotalCost(totalCost);
        rental.setStatus(RentalStatus.COMPLETED);

        Rental saved = rentalRepository.save(rental);
        bicycleService.updateBicycleStatus(rental.getBicycle().getId(), BicycleStatus.AVAILABLE);

        try {
            CalculateRatingRequest grpcRequest = CalculateRatingRequest.newBuilder()
                    .setRentalId(saved.getId())
                    .setTotalCost(saved.getTotalCost())
                    .build();

            CalculateRatingResponse grpcResponse = statsClient.calculateRentalRating(grpcRequest);

            RentalRatedEvent event = new RentalRatedEvent(
                    saved.getId(),
                    saved.getCustomer().getId(),
                    grpcResponse.getLevel(),
                    grpcResponse.getScore()
            );

            rabbitTemplate.convertAndSend(RabbitMQConfig.FANOUT_EXCHANGE, "", event);

        } catch (Exception e) {
            System.err.println("WARNING: Не удалось рассчитать рейтинг: " + e.getMessage());
        }

        return modelMapper.map(saved, RentalResponse.class);
    }
}