package com.example.pricingservice.service;


import com.example.grpc.pricing.CalculatePriceRequest;
import com.example.grpc.pricing.CalculatePriceResponse;
import com.example.grpc.pricing.PricingServiceGrpc;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.ArrayList;
import java.util.List;

@GrpcService
public class PricingServiceImpl extends PricingServiceGrpc.PricingServiceImplBase {

    @Override
    public void calculatePrice(CalculatePriceRequest request, StreamObserver<CalculatePriceResponse> responseObserver) {
        if (request.getEndTimestamp() < request.getStartTimestamp()) {
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription("End time cannot be earlier than start time")
                    .asRuntimeException());
            return;
        }
        if (request.getBasePricePerHour() <= 0) {
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription("Price per hour should be positive")
                    .asRuntimeException());
            return;
        }
        double baseRate = request.getBasePricePerHour();
        long startSeconds = request.getStartTimestamp();
        long endSeconds = request.getEndTimestamp();
        int loyaltyPoints = request.getLoyaltyPoints();

        double durationHours = Math.ceil((endSeconds - startSeconds) / 3600.0);
        if (durationHours < 1) durationHours = 1;
        double finalPrice = baseRate * durationHours;
        List<String> details = new ArrayList<>();

        if (durationHours > 5) {
            finalPrice *= 0.85;
            details.add("-15%");
        }
        if (loyaltyPoints > 100) {
            finalPrice *= 0.9;
            details.add("-10%");
        } else if (loyaltyPoints > 50) {
            finalPrice *= 0.95;
            details.add("-5%");
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