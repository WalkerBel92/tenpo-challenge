package com.tenpo.challenge.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

/**
 * Model class for call logs.
 * This class represents a log entry for an API call.
 * It includes details such as the timestamp, endpoint, parameters, response, error, and status code.
 *
 * @author bbeltran
 * @version 1.0
 * @since 2023-10-05
 */
@Getter
@Setter
@Table("call_logs")
public class CallLog {

    @Id
    private Long id;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private LocalDateTime timestamp;
    private String endpoint;
    private String parameters;
    private String response;
    private String error;
    private Integer statusCode;
}