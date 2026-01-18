package com.example.pricingservice.service;


import com.example.grpc.pricing.CalculatePriceRequest;
import com.example.grpc.pricing.CalculatePriceResponse;
import com.example.grpc.pricing.PricingServiceGrpc;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@GrpcService
public class PricingServiceImpl extends PricingServiceGrpc.PricingServiceImplBase {

    @Override
    public void calculatePrice(CalculatePriceRequest request, StreamObserver<CalculatePriceResponse> responseObserver) {
        double baseRate = request.getBasePricePerHour();
        long startSeconds = request.getStartTimestamp();
        long endSeconds = request.getEndTimestamp();
        int loyaltyPoints = request.getLoyaltyPoints();

        double durationHours = Math.ceil((endSeconds - startSeconds) / 3600.0);
        if (durationHours < 1) durationHours = 1;

        double finalPrice = baseRate * durationHours;
        List<String> details = new ArrayList<>();
        details.add(String.format("Base rate: %.2f x %.0f hours = %.2f", baseRate, durationHours, finalPrice));

        LocalDateTime startDate = LocalDateTime.ofInstant(Instant.ofEpochSecond(startSeconds), ZoneId.systemDefault());
        DayOfWeek day = startDate.getDayOfWeek();
        if (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) {
            finalPrice *= 1.2;
            details.add("Weekend surcharge (+20%)");
        }

        if (durationHours > 5) {
            finalPrice *= 0.85;
            details.add("Long duration discount (-15%)");
        }

        if (loyaltyPoints > 100) {
            finalPrice *= 0.9;
            details.add("Gold Level (-10%)");
        } else if (loyaltyPoints > 50) {
            finalPrice *= 0.95;
            details.add("Silver Level (-5%)");
        } else {
            details.add("No discount (Low loyalty points)");
        }

        finalPrice = Math.round(finalPrice * 100.0) / 100.0;

        CalculatePriceResponse response = CalculatePriceResponse.newBuilder()
                .setFinalPrice(finalPrice)
                .setCalculationDetails(String.join(", ", details))
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}