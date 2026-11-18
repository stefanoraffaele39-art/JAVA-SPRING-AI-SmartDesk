package com.stefano.smartdesk.common.dto;

import java.time.Instant;
import java.util.Map;

import org.springframework.http.HttpStatus;

public record ErrorResponse(
    Instant timestamp,
    int status,
    String error,
    String message,
    String path,
    Map<String, String> fieldErrors
) {
    public static ErrorResponse of(HttpStatus status, String message, String path){
        return new ErrorResponse(Instant.now(), 
        status.value(), 
        status.getReasonPhrase(), 
        message, 
        path, 
        null);
    }
}
