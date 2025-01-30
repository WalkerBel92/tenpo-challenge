package com.tenpo.challenge.controller;

import com.tenpo.challenge.service.CalculationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * Calculation controller for the application.
 * This class handles HTTP requests for calculations.
 *
 * @author bbeltran
 * @version 1.0
 * @since 2023-10-05
 */
@RestController
@RequestMapping("/calculation")
public class CalculationController {

    private final CalculationService calculationService;

    public CalculationController(CalculationService calculationService) {
        this.calculationService = calculationService;
    }

    /**
     * Handles GET requests to perform a calculation.
     *
     * @param number1 the first number for the calculation.
     * @param number2 the second number for the calculation.
     * @return a Mono containing the result of the calculation.
     */
    @GetMapping("/")
    public Mono<ResponseEntity<Double>> calculate(@RequestParam Double number1, @RequestParam Double number2) {
        return calculationService.calculate(number1, number2)
                .map(ResponseEntity::ok)
                .onErrorResume(Mono::error);
    }
}
