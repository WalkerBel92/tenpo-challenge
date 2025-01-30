package com.tenpo.challenge.controller;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.tenpo.challenge.service.CalculationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import org.springframework.http.ResponseEntity;

public class CalculationControllerTest {

    @Mock
    private CalculationService calculationService;

    @InjectMocks
    private CalculationController calculationController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCalculate() {
        // Configura el comportamiento del servicio simulado
        when(calculationService.calculate(12.0, 50.0)).thenReturn(Mono.just(68.2));

        // Llama al método y verifica el resultado
        Mono<ResponseEntity<Double>> response = calculationController.calculate(12.0, 50.0);
        assertEquals(68.2, response.block().getBody());
        assertEquals(200, response.block().getStatusCodeValue());
    }
}