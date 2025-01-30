package com.tenpo.challenge.service;

import reactor.core.publisher.Mono;

/**
 * Service interface for calculations.
 * This interface defines a method to perform calculations using two numbers.
 *
 * @author bbeltran
 * @version 1.0
 * @since 2023-10-05
 */
public interface CalculationService {

    /**
     * Calculates the result by adding two numbers and applying an external percentage.
     *
     * @param number1 the first number.
     * @param number2 the second number.
     * @return a Mono<Double> containing the calculated result.
     */
    Mono<Double> calculate(Double number1, Double number2);
}
