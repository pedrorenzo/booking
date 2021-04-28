package com.pedrorenzo.booking.controllers;

import com.pedrorenzo.booking.dtos.BookingRequestDTO;
import com.pedrorenzo.booking.dtos.BookingResponseDTO;
import com.pedrorenzo.booking.response.Response;
import com.pedrorenzo.booking.services.BookingService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/v1/bookings")
public class BookingController {

    private static final Logger LOGGER = LoggerFactory.getLogger(BookingController.class);

    private final BookingService bookingService;

    @Autowired
    public BookingController(final BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @ApiOperation(value = "Insert a new booking")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Booking inserted successfully"),
            @ApiResponse(code = 400, message = "Ops...something went wrong with your request")
    })
    @ResponseStatus(code = HttpStatus.CREATED)
    @PostMapping
    public ResponseEntity<Response<BookingResponseDTO>> insert(@RequestBody @ApiParam(value = "The booking data")
                                                               @Valid final BookingRequestDTO bookingRequestDTO) {
        LOGGER.info("Inserting booking: {}.", bookingRequestDTO);

        final Response<BookingResponseDTO> response = new Response<>();
        final BookingResponseDTO bookingResponseDTO = bookingService.insert(bookingRequestDTO);
        response.setData(bookingResponseDTO);

        return ResponseEntity.created(URI.create("/v1/bookings/" + bookingResponseDTO.getId())).
                body(response);
    }

    @ApiOperation(value = "Find a booking by its id")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Booking found successfully"),
            @ApiResponse(code = 404, message = "Booking not found")
    })
    @GetMapping(value = "/{id}")
    public ResponseEntity<Response<BookingResponseDTO>> findById(@PathVariable("id") @ApiParam(value = "The booking id")
                                                                     final String id) {
        LOGGER.info("Finding booking by id: {}.", id);

        final Response<BookingResponseDTO> response = new Response<>();
        response.setData(bookingService.findById(id));

        return ResponseEntity.ok(response);
    }

    @ApiOperation(value = "Find all bookings")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Bookings found successfully")
    })
    @GetMapping
    public ResponseEntity<Response<List<BookingResponseDTO>>> findAll() {
        //
        // For the sake of simplicity, I considered that when executing this request, the user can see all
        // registered bookings without any type of filter.
        //
        LOGGER.info("Finding all bookings.");

        final Response<List<BookingResponseDTO>> response = new Response<>();
        response.setData(bookingService.findAll());

        return ResponseEntity.ok(response);
    }

    @ApiOperation(value = "Delete a booking by its id")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "No Content"),
            @ApiResponse(code = 404, message = "Booking not found")
    })
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable("id") @ApiParam(value = "The booking id")
                                                                 final String id) {
        LOGGER.info("Deleting booking by id: {}.", id);

        bookingService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @ApiOperation(value = "Update a booking by its id")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "No Content"),
            @ApiResponse(code = 404, message = "Booking not found"),
            @ApiResponse(code = 400, message = "Ops...something went wrong with your request")
    })
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    @PutMapping(value = "/{id}")
    public ResponseEntity<Void> update(@RequestBody @ApiParam(value = "The booking data")
                                           @Valid final BookingRequestDTO bookingRequestDTO,
                                       @PathVariable("id") @ApiParam(value = "The booking id")
                                           final String id) {
        LOGGER.info("Updating booking with id {} and body request {}.", id, bookingRequestDTO);

        bookingService.update(id, bookingRequestDTO);
        return ResponseEntity.noContent().build();
    }

}
