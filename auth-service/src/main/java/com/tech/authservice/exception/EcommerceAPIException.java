package com.tech.authservice.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public class EcommerceAPIException extends RuntimeException {

    @Getter
    private final HttpStatus status;
    private final String message;

    public EcommerceAPIException(HttpStatus status, String message) {
        super(message);
        this.status = status;
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}