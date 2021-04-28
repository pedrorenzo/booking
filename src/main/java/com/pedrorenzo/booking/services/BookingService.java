package com.pedrorenzo.booking.services;

import com.pedrorenzo.booking.dtos.BookingRequestDTO;
import com.pedrorenzo.booking.dtos.BookingResponseDTO;

import java.util.List;

public interface BookingService {

    /**
     * Inserts a booking.
     *
     * @param bookingRequestDTO The booking to be inserted.
     * @return The inserted booking.
     */
    BookingResponseDTO insert(final BookingRequestDTO bookingRequestDTO);

    /**
     * Finds a booking by its id.
     *
     * @param id The booking id.
     * @return The booking found.
     */
    BookingResponseDTO findById(final String id);

    /**
     * Finds all bookings.
     *
     * @return The bookings found.
     */
    List<BookingResponseDTO> findAll();

    /**
     * Deletes a booking by its id.
     *
     * @param id The booking id.
     */
    void deleteById(final String id);

    /**
     * Updates a booking.
     *
     * @param id The booking id.
     * @param bookingRequestDTO The request to update.
     */
    void update(final String id, final BookingRequestDTO bookingRequestDTO);

}
