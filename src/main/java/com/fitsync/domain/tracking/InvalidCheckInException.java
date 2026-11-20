package com.fitsync.domain.tracking;

public class InvalidCheckInException extends RuntimeException {
    public InvalidCheckInException(String message) {
        super(message);
    }
}
