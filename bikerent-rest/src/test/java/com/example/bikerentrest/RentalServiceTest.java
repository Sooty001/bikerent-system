package com.example.bikerentrest;

import com.example.bikerentrest.entities.Bicycle;
import com.example.bikerentrest.entities.Customer;
import com.example.bikerentrest.entities.Rental;
import com.example.bikerentrest.repositories.BicycleRepository;
import com.example.bikerentrest.repositories.CustomerRepository;
import com.example.bikerentrest.repositories.RentalRepository;
import com.example.bikerentrest.services.RentalService;
import com.example.grpc.stats.CalculateRatingRequest;
import com.example.grpc.stats.CalculateRatingResponse;
import com.example.grpc.stats.StatisticsServiceGrpc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class RentalServiceTest {

    @Autowired
    private RentalService rentalService;

    @Autowired
    private RentalRepository rentalRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private BicycleRepository bicycleRepository;

    @MockitoBean
    private RabbitTemplate rabbitTemplate;

    private StatisticsServiceGrpc.StatisticsServiceBlockingStub statsClientMock;

    @BeforeEach
    void setUp() {
        statsClientMock = Mockito.mock(StatisticsServiceGrpc.StatisticsServiceBlockingStub.class);

        ReflectionTestUtils.setField(rentalService, "statsClient", statsClientMock);
    }

    @Test
    void completeRental_ShouldSaveToDbAndSendNotification() {
        Customer customer = customerRepository.save(new Customer("Test", "User", "I", "89990000000", "test@mail.ru"));
        Bicycle bicycle = bicycleRepository.save(new Bicycle("Bike-1", "MTB", "L", 100.0, "Desc", "url"));

        Rental rental = new Rental(customer, bicycle, LocalDateTime.now().plusHours(1));
        rental.setStartTime(LocalDateTime.now().minusHours(2));
        rental = rentalRepository.save(rental);

        CalculateRatingResponse fakeResponse = CalculateRatingResponse.newBuilder()
                .setLevel("GOLD")
                .setScore(100)
                .build();

        when(statsClientMock.calculateRentalRating(any(CalculateRatingRequest.class)))
                .thenReturn(fakeResponse);

        var result = rentalService.completeRental(rental.getId());

        assertEquals("COMPLETED", result.getStatus());

        verify(statsClientMock).calculateRentalRating(any(CalculateRatingRequest.class));

        verify(rabbitTemplate).convertAndSend(anyString(), anyString(), any(Object.class));
    }
}