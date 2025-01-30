package com.tenpo.challenge.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.WebFluxConfigurer;

/**
 * SpringDoc configuration for the application.
 * This class enables WebFlux and defines a bean for GroupedOpenApi
 * to document the public API.
 *
 * @author bbeltran
 * @version 1.0
 * @since 2023-10-05
 */
@Configuration
@EnableWebFlux
public class SpringDocConfig implements WebFluxConfigurer {

    /**
     * Creates a GroupedOpenApi bean to document the public API.
     *
     * @return a configured GroupedOpenApi.
     */
    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("public")
                .pathsToMatch("/**")
                .build();
    }
}