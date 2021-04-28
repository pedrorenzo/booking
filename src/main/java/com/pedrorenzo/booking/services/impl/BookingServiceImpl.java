package com.pedrorenzo.booking.services.impl;

import com.pedrorenzo.booking.dtos.BookingRequestDTO;
import com.pedrorenzo.booking.dtos.BookingResponseDTO;
import com.pedrorenzo.booking.entities.Booking;
import com.pedrorenzo.booking.exceptions.BookingNotFoundException;
import com.pedrorenzo.booking.exceptions.InvalidBookingException;
import com.pedrorenzo.booking.repositories.BookingRepository;
import com.pedrorenzo.booking.services.BookingService;
import com.pedrorenzo.booking.utils.ConverterUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.pedrorenzo.booking.utils.Constants.MAXIMUM_DIFF_DAYS_IN_ADVANCE;
import static com.pedrorenzo.booking.utils.Constants.MAXIMUM_DIFF_STAY_DAYS;
import static com.pedrorenzo.booking.utils.ConverterUtils.covertBooking;
import static com.pedrorenzo.booking.utils.ConverterUtils.covertDTOUpdatingUpdatedDate;
import static com.pedrorenzo.booking.utils.ConverterUtils.covertDTO;
import static com.pedrorenzo.booking.utils.ErrorMessages.*;

@Service
public class BookingServiceImpl implements BookingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BookingServiceImpl.class);

    private final BookingRepository bookingRepository;

    @Autowired
    public BookingServiceImpl(final BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    @Override
    public BookingResponseDTO insert(final BookingRequestDTO bookingRequestDTO) {
        validateBookingRequest(bookingRequestDTO);
        LOGGER.info("Booking {} insertion validated.", bookingRequestDTO);

        final Booking booking = bookingRepository.insert(covertDTO(bookingRequestDTO));
        return covertBooking(booking);
    }

    @Override
    public BookingResponseDTO findById(final String id) {
        Optional<Booking> booking = bookingRepository.findById(id);
        if (!booking.isPresent()) {
            throw new BookingNotFoundException(BOOKING_NOT_FOUND);
        }
        LOGGER.info("Booking id {} present, returning...", id);
        return covertBooking(booking.get());
    }

    @Override
    public List<BookingResponseDTO> findAll() {
        List<Booking> bookings = bookingRepository.findAll();

        return bookings.stream().map(ConverterUtils::covertBooking).collect(Collectors.toList());
    }

    @Override
    public void deleteById(final String id) {
        final Optional<Booking> booking = bookingRepository.findById(id);
        if (!booking.isPresent()) {
            throw new BookingNotFoundException(BOOKING_NOT_FOUND);
        }

        bookingRepository.delete(booking.get());
    }

    @Override
    public void update(final String id, final BookingRequestDTO bookingRequestDTO) {
        final Optional<Booking> booking = bookingRepository.findById(id);
        if (!booking.isPresent()) {
            throw new BookingNotFoundException(BOOKING_NOT_FOUND);
        }

        validateBookingRequest(bookingRequestDTO);
        LOGGER.info("Booking {} update validated.", bookingRequestDTO);

        bookingRepository.save(covertDTOUpdatingUpdatedDate(booking.get(), bookingRequestDTO));
    }

    /**
     * Validates the booking request based on the business rules.
     *
     * @param bookingRequestDTO The request to be validated.
     */
    private void validateBookingRequest(final BookingRequestDTO bookingRequestDTO) {
        final LocalDate now = OffsetDateTime.now(ZoneOffset.UTC).toLocalDate();
        if (bookingRequestDTO.getFromDate().isAfter(bookingRequestDTO.getToDate())) {
            throw new InvalidBookingException(FROM_DATE_AFTER_TO_DATE);
        }

        if (bookingRequestDTO.getFromDate().equals(now) || bookingRequestDTO.getFromDate().isBefore(now)) {
            throw new InvalidBookingException(STAY_START_INVALID);
        }

        long diffInDays = ChronoUnit.DAYS.between(bookingRequestDTO.getFromDate(), bookingRequestDTO.getToDate());
        if (diffInDays > MAXIMUM_DIFF_STAY_DAYS) {
            throw new InvalidBookingException(STAY_LONGER_THAN_LIMIT);
        }

        diffInDays = ChronoUnit.DAYS.between(now, bookingRequestDTO.getFromDate());
        if (diffInDays > MAXIMUM_DIFF_DAYS_IN_ADVANCE) {
            throw new InvalidBookingException(DAYS_IN_ADVANCE_LONGER_THAN_LIMIT);
        }

        if (!areDaysAvailable(bookingRequestDTO)) {
            throw new InvalidBookingException(BOOKING_NOT_AVAILABLE);
        }
    }

    /**
     * Validates if the days from the request are available: If the room is not already booked.
     *
     * @param bookingRequestDTO The request to be validated.
     * @return <b>true</b> if the days are available, <b>false</b> otherwise.
     */
    private boolean areDaysAvailable(final BookingRequestDTO bookingRequestDTO) {
        final List<BookingResponseDTO> bookingResponseDTOs = findAll();
        final LocalDate requestFromDate = bookingRequestDTO.getFromDate();
        final LocalDate requestToDate = bookingRequestDTO.getToDate();

        LocalDate responseFromDate;
        LocalDate responseToDate;
        for (BookingResponseDTO response : bookingResponseDTOs) {
            responseFromDate = response.getFromDate();
            responseToDate = response.getToDate();

            // The last day of the booking is before the first of the request. We don't need to worry w/ this booking.
            if (responseToDate.isBefore(requestFromDate)) {
                continue;
            }

            // If "from" or "to" from the request is equals to "from" or "to" from an existing booking, not available.
            if (requestFromDate.isEqual(responseFromDate) ||
                    requestFromDate.isEqual(responseToDate) ||
                    requestToDate.isEqual(responseFromDate) ||
                    requestToDate.isEqual(responseToDate)) {
                return false;
            }

            // If "from" or "to" from the request is in between "from" or "to" from an existing booking, not available.
            if ((requestFromDate.isAfter(responseFromDate) &&
                    requestFromDate.isBefore(responseToDate)) ||
                    (requestToDate.isAfter(responseFromDate) &&
                            requestToDate.isBefore(responseToDate))) {
                return false;
            }

            // If "from" and "to" from the response are in between the "from" and "to" from the request, not available.
            if (responseFromDate.isAfter(requestFromDate) &&
                    responseFromDate.isBefore(requestToDate) &&
                    responseToDate.isAfter(requestFromDate) &&
                    responseToDate.isBefore(requestToDate)) {
                return false;
            }
        }
        return true;
    }

}
