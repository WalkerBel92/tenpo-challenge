package com.tenpo.challenge.repository;

import com.tenpo.challenge.model.CallLog;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

/**
 * Repository interface for call logs.
 * This interface provides reactive CRUD operations for CallLog entities.
 * It also includes a method to find all call logs ordered by timestamp in descending order.
 *
 * @author bbeltran
 * @version 1.0
 * @since 2023-10-05
 */
public interface CallLogRepository extends ReactiveCrudRepository<CallLog, Long> {

    /**
     * Finds all call logs ordered by timestamp in descending order.
     *
     * @param pageable the pagination information.
     * @return a Flux of CallLog entities.
     */
    Flux<CallLog> findAllByOrderByTimestampDesc(Pageable pageable);
}