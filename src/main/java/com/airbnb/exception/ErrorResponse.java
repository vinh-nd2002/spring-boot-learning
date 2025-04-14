package com.airbnb.exception;

import java.time.Instant;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Builder
@Getter
@Setter
public class ErrorResponse {

    @Builder.Default
    private Instant timestamp = Instant.now();
    private String message;
    private String path;
}
