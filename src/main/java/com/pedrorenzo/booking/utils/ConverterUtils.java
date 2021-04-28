package com.pedrorenzo.booking.utils;

import com.pedrorenzo.booking.dtos.BookingRequestDTO;
import com.pedrorenzo.booking.dtos.BookingResponseDTO;
import com.pedrorenzo.booking.entities.Booking;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public class ConverterUtils {

    /**
     * Coverts a {@link BookingResponseDTO} into a {@link Booking}.
     *
     * @param bookingRequestDTO The booking to be converted.
     * @return The converted booking.
     */
    public static Booking covertDTO(final BookingRequestDTO bookingRequestDTO) {
        final OffsetDateTime now = OffsetDateTime.now();
        return new Booking(bookingRequestDTO.getFromDate().atStartOfDay().toInstant(ZoneOffset.UTC),
                bookingRequestDTO.getToDate().atTime(LocalTime.MIDNIGHT).toInstant(ZoneOffset.UTC),
                now.toInstant(), now.toInstant());
    }

    /**
     * Coverts a {@link Booking} into a {@link BookingResponseDTO}.
     *
     * @param booking The booking to be converted.
     * @return The converted booking.
     */
    public static BookingResponseDTO covertBooking(final Booking booking) {
        return new BookingResponseDTO(booking.getId(),
                LocalDateTime.ofInstant(booking.getFromDate(), ZoneOffset.UTC).toLocalDate(),
                LocalDateTime.ofInstant(booking.getToDate(), ZoneOffset.UTC).toLocalDate());
    }

    /**
     * Coverts a {@link BookingResponseDTO} into a {@link Booking}, updating the updated date as now.
     *
     * @param bookingRequestDTO The booking to be converted.
     * @return The converted booking.
     */
    public static Booking covertDTOUpdatingUpdatedDate(final Booking booking,
                                                       final BookingRequestDTO bookingRequestDTO) {
        booking.setFromDate(bookingRequestDTO.getFromDate().atStartOfDay().toInstant(ZoneOffset.UTC));
        booking.setToDate(bookingRequestDTO.getToDate().atTime(LocalTime.MIDNIGHT).toInstant(ZoneOffset.UTC));
        booking.setUpdatedDate(OffsetDateTime.now().toInstant());
        return booking;
    }

}
