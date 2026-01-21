package com.example.bikerentcore.services.grpc;

import com.example.bikerentapi.exception.ExternalServiceException;
import com.example.grpc.pricing.CalculatePriceRequest;
import com.example.grpc.pricing.CalculatePriceResponse;
import com.example.grpc.pricing.PricingServiceGrpc;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Service
public class PricingGrpcClient {

    private static final Logger log = LoggerFactory.getLogger(PricingGrpcClient.class);

    @GrpcClient("pricing-service")
    private PricingServiceGrpc.PricingServiceBlockingStub pricingStub;

    public double calculatePrice(UUID bicycleId, UUID customerId, double pricePerHour, int loyaltyPoints,
                                 LocalDateTime startTime, LocalDateTime endTime) {
        try {
            CalculatePriceRequest request = CalculatePriceRequest.newBuilder()
                    .setBicycleId(bicycleId.toString())
                    .setUserId(customerId.toString())
                    .setBasePricePerHour(pricePerHour)
                    .setStartTimestamp(startTime.toEpochSecond(ZoneOffset.UTC))
                    .setEndTimestamp(endTime.toEpochSecond(ZoneOffset.UTC))
                    .setLoyaltyPoints(loyaltyPoints)
                    .build();

            CalculatePriceResponse response = pricingStub.calculatePrice(request);

            log.info("Price: {}, Details: {}", response.getFinalPrice(), response.getCalculationDetails());

            return response.getFinalPrice();

        } catch (StatusRuntimeException e) {
            String serverMessage = e.getStatus().getDescription();
            if (e.getStatus().getCode() == Status.Code.INVALID_ARGUMENT) {
                throw new IllegalArgumentException(serverMessage);
            } else {
                throw new ExternalServiceException("Pricing Service", serverMessage);
            }
        }
    }
}
