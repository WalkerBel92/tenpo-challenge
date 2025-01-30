package com.tenpo.challenge.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.tenpo.challenge.service.impl.CalculationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;

public class CalculationServiceImplTest {

    @Mock
    private ExternalPercentageService externalPercentageService;

    @InjectMocks
    private CalculationServiceImpl calculationService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCalculate() {
        // Configura el comportamiento del servicio simulado
        when(externalPercentageService.getPercentage()).thenReturn(Mono.just(10.0));

        // Llama al método y verifica el resultado
        Mono<Double> result = calculationService.calculate(12.0, 50.0);
        assertEquals(68.2, result.block());
    }
}