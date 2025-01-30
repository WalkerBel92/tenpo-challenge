package com.tenpo.challenge.service;

import reactor.core.publisher.Mono;

/**
 * Service interface for retrieving external percentages.
 * This interface defines a method to get a percentage from an external service.
 * The percentage is used in various calculations within the application.
 * Implementations of this interface should handle the retrieval and any necessary caching of the percentage.
 *
 * @author bbeltran
 * @version 1.0
 * @since 2023-10-05
 */
public interface ExternalPercentageService {

    /**
     * Retrieves the percentage from an external service.
     *
     * @return a Mono<Double> containing the percentage.
     */
    Mono<Double> getPercentage();
}
