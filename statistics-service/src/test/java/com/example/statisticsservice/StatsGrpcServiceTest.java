package com.example.statisticsservice;

import com.example.grpc.stats.CalculateRatingRequest;
import com.example.grpc.stats.CalculateRatingResponse;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

class StatsGrpcServiceTest {

    @Test
    void calculateRentalRating_ShouldReturnGold_WhenCostIsHigh() {
        StatsStore store = new StatsStore();
        StatsGrpcService service = new StatsGrpcService(store);

        CalculateRatingRequest request = CalculateRatingRequest.newBuilder()
                .setRentalId(1L)
                .setTotalCost(600.0)
                .build();

        StreamObserver<CalculateRatingResponse> responseObserver = Mockito.mock(StreamObserver.class);

        service.calculateRentalRating(request, responseObserver);

        ArgumentCaptor<CalculateRatingResponse> captor = ArgumentCaptor.forClass(CalculateRatingResponse.class);
        verify(responseObserver).onNext(captor.capture());
        verify(responseObserver).onCompleted();

        CalculateRatingResponse response = captor.getValue();

        assertEquals("GOLD", response.getLevel());
        assertEquals(6000, response.getScore());
    }
}
