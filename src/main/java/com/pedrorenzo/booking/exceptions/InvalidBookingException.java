package com.pedrorenzo.booking.exceptions;

public class InvalidBookingException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public InvalidBookingException(final String message) {
        super(message);
    }

}