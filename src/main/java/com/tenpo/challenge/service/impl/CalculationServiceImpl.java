package com.tenpo.challenge.service.impl;

import com.tenpo.challenge.service.CalculationService;
import com.tenpo.challenge.service.ExternalPercentageService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * Service implementation for calculations.
 * This service performs calculations using two numbers and an external percentage.
 * It retrieves the percentage from an external service and applies it to the sum of the two numbers.
 *
 * @author bbeltran
 * @version 1.0
 * @since 2023-10-05
 */
@Service
public class CalculationServiceImpl implements CalculationService {

    private final ExternalPercentageService externalPercentageService;

    /**
     * Constructs a new CalculationServiceImpl with the specified ExternalPercentageService.
     *
     * @param externalPercentageService the service used to retrieve the external percentage.
     */
    public CalculationServiceImpl(ExternalPercentageService externalPercentageService) {
        this.externalPercentageService = externalPercentageService;
    }

    /**
     * Calculates the result by adding two numbers and applying an external percentage.
     *
     * @param number1 the first number.
     * @param number2 the second number.
     * @return a Mono<Double> containing the calculated result.
     */
    @Override
    public Mono<Double> calculate(Double number1, Double number2) {
        return externalPercentageService.getPercentage()
                .map(percentage -> number1 + number2 + (number1 + number2) * percentage/100);
    }
}
