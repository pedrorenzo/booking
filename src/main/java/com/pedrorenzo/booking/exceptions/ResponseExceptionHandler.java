package com.pedrorenzo.booking.exceptions;

import com.pedrorenzo.booking.response.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Collections;

@ControllerAdvice
@RestController
public class ResponseExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<Response> handleAllExceptions(final Exception ex) {
        return new ResponseEntity<>(new Response(Collections.singletonList(ex.getMessage())),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(InvalidBookingException.class)
    public final ResponseEntity<Response> handleInvalidBookingException(final InvalidBookingException ex) {
        return new ResponseEntity<>(new Response(Collections.singletonList(ex.getMessage())),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BookingNotFoundException.class)
    public final ResponseEntity<Response> handleBookingNotFoundException(final BookingNotFoundException ex) {
        return new ResponseEntity<>(new Response(Collections.singletonList(ex.getMessage())),
                HttpStatus.NOT_FOUND);
    }

}