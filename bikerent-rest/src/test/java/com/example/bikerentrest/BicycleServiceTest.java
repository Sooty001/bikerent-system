package com.example.bikerentrest;

import com.example.bikerentapi.dto.request.BicycleRequest;
import com.example.bikerentapi.dto.response.BicycleResponse;
import com.example.bikerentapi.dto.response.PagedResponse;
import com.example.bikerentapi.exception.ResourceNotFoundException;
import com.example.bikerentrest.entities.Bicycle;
import com.example.bikerentrest.repositories.BicycleRepository;
import com.example.bikerentrest.services.BicycleService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BicycleServiceTest {

    @Mock
    private BicycleRepository bicycleRepository;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private BicycleService bicycleService;

    @Test
    void addBicycle_ShouldReturnResponse() {
        BicycleRequest request = new BicycleRequest("Model X", "Road", "L", 10.0, "Desc", "url");
        Bicycle bicycleEntity = new Bicycle("Model X", "Road", "L", 10.0, "Desc", "url");
        BicycleResponse responseDto = new BicycleResponse();

        when(modelMapper.map(request, Bicycle.class)).thenReturn(bicycleEntity);
        when(bicycleRepository.save(any(Bicycle.class))).thenReturn(bicycleEntity);
        when(modelMapper.map(bicycleEntity, BicycleResponse.class)).thenReturn(responseDto);

        BicycleResponse result = bicycleService.addBicycle(request);

        assertNotNull(result);
        verify(bicycleRepository).save(any(Bicycle.class));
    }

    @Test
    void findById_WhenExists_ShouldReturnBicycle() {
        Long id = 1L;
        Bicycle bicycle = new Bicycle("M", "T", "S", 1.0, "D", "U");
        BicycleResponse responseDto = new BicycleResponse();

        when(bicycleRepository.findById(id)).thenReturn(Optional.of(bicycle));
        when(modelMapper.map(bicycle, BicycleResponse.class)).thenReturn(responseDto);

        BicycleResponse result = bicycleService.findById(id);

        assertNotNull(result);
    }

    @Test
    void findById_WhenNotExists_ShouldThrowException() {
        Long id = 99L;
        when(bicycleRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bicycleService.findById(id));
    }

    @Test
    void findAll_ShouldReturnPagedResponse() {
        Bicycle bike = new Bicycle("M", "T", "S", 1.0, "D", "U");
        Page<Bicycle> page = new PageImpl<>(List.of(bike));

        when(bicycleRepository.findByDeletedFalse(any(Pageable.class))).thenReturn(page);
        when(modelMapper.map(any(), eq(BicycleResponse.class))).thenReturn(new BicycleResponse());

        PagedResponse<BicycleResponse> result = bicycleService.findAll(null, 0, 10);

        assertEquals(1, result.totalElements());
        assertEquals(1, result.totalPages());
    }

    @Test
    void deleteById_ShouldMarkDeletedAndSendEvent() {
        Long id = 1L;
        Bicycle bicycle = new Bicycle("M", "T", "S", 1.0, "D", "U");

        when(bicycleRepository.findById(id)).thenReturn(Optional.of(bicycle));

        bicycleService.deleteById(id);

        assertTrue(bicycle.isDeleted());
        verify(bicycleRepository).save(bicycle);

        verify(rabbitTemplate).convertAndSend(anyString(), anyString(), any(Object.class));
    }
}
