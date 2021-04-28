package com.pedrorenzo.booking.services.impl;

import com.pedrorenzo.booking.dtos.BookingRequestDTO;
import com.pedrorenzo.booking.dtos.BookingResponseDTO;
import com.pedrorenzo.booking.entities.Booking;
import com.pedrorenzo.booking.exceptions.BookingNotFoundException;
import com.pedrorenzo.booking.exceptions.InvalidBookingException;
import com.pedrorenzo.booking.repositories.BookingRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.pedrorenzo.booking.utils.ErrorMessages.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static com.pedrorenzo.booking.utils.ConverterUtils.covertBooking;

@ExtendWith(MockitoExtension.class)
public class BookingServiceImplTest {

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Mock
    private BookingRepository bookingRepository;

    private static final LocalDate NOW = OffsetDateTime.now(ZoneOffset.UTC).toLocalDate();

    @Test
    public void testFindExistentBookingById() {
        final Booking expectedBooking = new Booking(Instant.now(), Instant.now(), Instant.now(), Instant.now());
        final BookingResponseDTO expectedBookingResponseDTO = covertBooking(expectedBooking);

        when(bookingRepository.findById("id")).thenReturn(Optional.of(expectedBooking));

        final BookingResponseDTO actualBookingResponseDTO = bookingService.findById("id");

        assertEquals(expectedBookingResponseDTO.getFromDate(), actualBookingResponseDTO.getFromDate());
        assertEquals(expectedBookingResponseDTO.getToDate(), actualBookingResponseDTO.getToDate());
    }

    @Test
    public void testFindNonExistentBookingById() {
        final BookingNotFoundException thrown = assertThrows(
                BookingNotFoundException.class,
                () -> bookingService.findById("id"),
                "Expected findById() to throw BookingNotFoundException"
        );

        assertTrue(thrown.getMessage().contains(BOOKING_NOT_FOUND));
    }

    @Test
    public void testFindAllBookings() {
        final Booking expectedBooking = new Booking(Instant.now(), Instant.now(), Instant.now(), Instant.now());
        final BookingResponseDTO expectedBookingResponseDTO = covertBooking(expectedBooking);
        final List<Booking> expectedBookings = new ArrayList<>();
        expectedBookings.add(expectedBooking);

        when(bookingRepository.findAll()).thenReturn(expectedBookings);

        List<BookingResponseDTO> actualBookingsResponseDTO = bookingService.findAll();
        assertEquals(expectedBookingResponseDTO.getFromDate(), actualBookingsResponseDTO.get(0).getFromDate());
        assertEquals(expectedBookingResponseDTO.getToDate(), actualBookingsResponseDTO.get(0).getToDate());
    }

    @Test
    public void testDeleteExistentBookingById() {
        final Booking expectedBooking = new Booking(Instant.now(), Instant.now(), Instant.now(), Instant.now());

        when(bookingRepository.findById("id")).thenReturn(Optional.of(expectedBooking));

        bookingService.deleteById("id");
        verify(bookingRepository, times(1)).delete(any(Booking.class));
    }

    @Test
    public void testDeleteNonExistentBookingById() {
        final BookingNotFoundException thrown = assertThrows(
                BookingNotFoundException.class,
                () -> bookingService.deleteById("id"),
                "Expected deleteById() to throw BookingNotFoundException"
        );

        assertTrue(thrown.getMessage().contains(BOOKING_NOT_FOUND));
    }

    @Test
    public void testInsertValidBooking() {
        final BookingRequestDTO bookingRequestDTO = new BookingRequestDTO(NOW.plusDays(1),
                NOW.plusDays(2));
        final Booking expectedBooking = new Booking(OffsetDateTime.now().minusDays(2).toInstant(),
                OffsetDateTime.now().minusDays(2).toInstant(), Instant.now(), Instant.now());
        final BookingResponseDTO expectedBookingResponseDTO = covertBooking(expectedBooking);
        final List<Booking> expectedBookings = new ArrayList<>();
        expectedBookings.add(expectedBooking);

        when(bookingRepository.findAll()).thenReturn(expectedBookings);
        when(bookingRepository.insert(any(Booking.class))).thenReturn(expectedBooking);

        final BookingResponseDTO actualBookingResponseDTO = bookingService.insert(bookingRequestDTO);
        assertEquals(expectedBookingResponseDTO.getFromDate(), actualBookingResponseDTO.getFromDate());
        assertEquals(expectedBookingResponseDTO.getToDate(), actualBookingResponseDTO.getToDate());
    }

    @Test
    public void testInsertBookingStayBiggerThan3Days() {
        final BookingRequestDTO bookingRequestDTO = new BookingRequestDTO(NOW.plusDays(1),
                NOW.plusDays(6));

        final InvalidBookingException thrown = assertThrows(
                InvalidBookingException.class,
                () -> bookingService.insert(bookingRequestDTO),
                "Expected insert() to throw InvalidBookingException"
        );

        assertTrue(thrown.getMessage().contains(STAY_LONGER_THAN_LIMIT));
    }

    @Test
    public void testInsertBookingDaysInAdvanceBiggerThan30() {
        final BookingRequestDTO bookingRequestDTO = new BookingRequestDTO(NOW.plusDays(31),
                NOW.plusDays(31));

        final InvalidBookingException thrown = assertThrows(
                InvalidBookingException.class,
                () -> bookingService.insert(bookingRequestDTO),
                "Expected insert() to throw InvalidBookingException"
        );

        assertTrue(thrown.getMessage().contains(DAYS_IN_ADVANCE_LONGER_THAN_LIMIT));
    }

    @Test
    public void testInsertBookingForToday() {
        final BookingRequestDTO bookingRequestDTO = new BookingRequestDTO(NOW,
                NOW.plusDays(1));

        final InvalidBookingException thrown = assertThrows(
                InvalidBookingException.class,
                () -> bookingService.insert(bookingRequestDTO),
                "Expected insert() to throw InvalidBookingException"
        );

        assertTrue(thrown.getMessage().contains(STAY_START_INVALID));
    }

    @Test
    public void testInsertBookingForYesterday() {
        final BookingRequestDTO bookingRequestDTO = new BookingRequestDTO(NOW.minusDays(1),
                NOW.plusDays(1));

        final InvalidBookingException thrown = assertThrows(
                InvalidBookingException.class,
                () -> bookingService.insert(bookingRequestDTO),
                "Expected insert() to throw InvalidBookingException"
        );

        assertTrue(thrown.getMessage().contains(STAY_START_INVALID));
    }

    @Test
    public void testInsertBookingFromAfterTo() {
        final BookingRequestDTO bookingRequestDTO = new BookingRequestDTO(NOW.plusDays(2),
                NOW.plusDays(1));

        final InvalidBookingException thrown = assertThrows(
                InvalidBookingException.class,
                () -> bookingService.insert(bookingRequestDTO),
                "Expected insert() to throw InvalidBookingException"
        );

        assertTrue(thrown.getMessage().contains(FROM_DATE_AFTER_TO_DATE));
    }

    @Test
    public void testInsertBookingToDateConflictingWithAnotherExistingToDate() {
        final OffsetDateTime now = OffsetDateTime.now();
        final Booking expectedBooking = new Booking(now.plusDays(2).toInstant(), now.plusDays(3).toInstant(),
                Instant.now(), Instant.now());
        final List<Booking> expectedBookings = new ArrayList<>();
        expectedBookings.add(expectedBooking);

        when(bookingRepository.findAll()).thenReturn(expectedBookings);

        final BookingRequestDTO bookingRequestDTO = new BookingRequestDTO(NOW.plusDays(1),
                NOW.plusDays(3));

        final InvalidBookingException thrown = assertThrows(
                InvalidBookingException.class,
                () -> bookingService.insert(bookingRequestDTO),
                "Expected insert() to throw InvalidBookingException"
        );

        assertTrue(thrown.getMessage().contains(BOOKING_NOT_AVAILABLE));
    }

    @Test
    public void testInsertBookingToDateConflictingWithAnotherExistingFromDate() {
        final OffsetDateTime now = OffsetDateTime.now();
        final Booking expectedBooking = new Booking(now.plusDays(2).toInstant(), now.plusDays(3).toInstant(),
                Instant.now(), Instant.now());
        final List<Booking> expectedBookings = new ArrayList<>();
        expectedBookings.add(expectedBooking);

        when(bookingRepository.findAll()).thenReturn(expectedBookings);

        final BookingRequestDTO bookingRequestDTO = new BookingRequestDTO(NOW.plusDays(2),
                NOW.plusDays(4));

        final InvalidBookingException thrown = assertThrows(
                InvalidBookingException.class,
                () -> bookingService.insert(bookingRequestDTO),
                "Expected insert() to throw InvalidBookingException"
        );

        assertTrue(thrown.getMessage().contains(BOOKING_NOT_AVAILABLE));
    }

    @Test
    public void testInsertBookingFromDateConflictingWithAnotherExistingFromDate() {
        final OffsetDateTime now = OffsetDateTime.now();
        final Booking expectedBooking = new Booking(now.plusDays(2).toInstant(), now.plusDays(4).toInstant(),
                Instant.now(), Instant.now());
        final List<Booking> expectedBookings = new ArrayList<>();
        expectedBookings.add(expectedBooking);

        when(bookingRepository.findAll()).thenReturn(expectedBookings);

        final BookingRequestDTO bookingRequestDTO = new BookingRequestDTO(NOW.plusDays(2),
                NOW.plusDays(3));

        final InvalidBookingException thrown = assertThrows(
                InvalidBookingException.class,
                () -> bookingService.insert(bookingRequestDTO),
                "Expected insert() to throw InvalidBookingException"
        );

        assertTrue(thrown.getMessage().contains(BOOKING_NOT_AVAILABLE));
    }

    @Test
    public void testInsertBookingFromDateConflictingWithAnotherExistingToDate() {
        final OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        final Booking expectedBooking = new Booking(now.plusDays(1).toInstant(), now.plusDays(2).toInstant(),
                Instant.now(), Instant.now());
        final List<Booking> expectedBookings = new ArrayList<>();
        expectedBookings.add(expectedBooking);

        when(bookingRepository.findAll()).thenReturn(expectedBookings);

        final BookingRequestDTO bookingRequestDTO = new BookingRequestDTO(NOW.plusDays(2),
                NOW.plusDays(3));

        final InvalidBookingException thrown = assertThrows(
                InvalidBookingException.class,
                () -> bookingService.insert(bookingRequestDTO),
                "Expected insert() to throw InvalidBookingException"
        );

        assertTrue(thrown.getMessage().contains(BOOKING_NOT_AVAILABLE));
    }

    @Test
    public void testInsertBookingFromDateBetweenAnotherExistingBooking() {
        final OffsetDateTime now = OffsetDateTime.now();
        final Booking expectedBooking = new Booking(now.plusDays(1).toInstant(), now.plusDays(3).toInstant(),
                Instant.now(), Instant.now());
        final List<Booking> expectedBookings = new ArrayList<>();
        expectedBookings.add(expectedBooking);

        when(bookingRepository.findAll()).thenReturn(expectedBookings);

        final BookingRequestDTO bookingRequestDTO = new BookingRequestDTO(NOW.plusDays(2),
                NOW.plusDays(4));

        final InvalidBookingException thrown = assertThrows(
                InvalidBookingException.class,
                () -> bookingService.insert(bookingRequestDTO),
                "Expected insert() to throw InvalidBookingException"
        );

        assertTrue(thrown.getMessage().contains(BOOKING_NOT_AVAILABLE));
    }

    @Test
    public void testInsertBookingToDateBetweenAnotherExistingBooking() {
        final OffsetDateTime now = OffsetDateTime.now();
        final Booking expectedBooking = new Booking(now.plusDays(2).toInstant(), now.plusDays(4).toInstant(),
                Instant.now(), Instant.now());
        final List<Booking> expectedBookings = new ArrayList<>();
        expectedBookings.add(expectedBooking);

        when(bookingRepository.findAll()).thenReturn(expectedBookings);

        final BookingRequestDTO bookingRequestDTO = new BookingRequestDTO(NOW.plusDays(1),
                NOW.plusDays(3));

        final InvalidBookingException thrown = assertThrows(
                InvalidBookingException.class,
                () -> bookingService.insert(bookingRequestDTO),
                "Expected insert() to throw InvalidBookingException"
        );

        assertTrue(thrown.getMessage().contains(BOOKING_NOT_AVAILABLE));
    }

    @Test
    public void testInsertBookingOverlappingTotallyAnotherExistingBooking() {
        final OffsetDateTime now = OffsetDateTime.now();
        final Booking expectedBooking = new Booking(now.plusDays(2).toInstant(), now.plusDays(2).toInstant(),
                Instant.now(), Instant.now());
        final List<Booking> expectedBookings = new ArrayList<>();
        expectedBookings.add(expectedBooking);

        when(bookingRepository.findAll()).thenReturn(expectedBookings);

        final BookingRequestDTO bookingRequestDTO = new BookingRequestDTO(NOW.plusDays(1),
                NOW.plusDays(3));

        final InvalidBookingException thrown = assertThrows(
                InvalidBookingException.class,
                () -> bookingService.insert(bookingRequestDTO),
                "Expected insert() to throw InvalidBookingException"
        );

        assertTrue(thrown.getMessage().contains(BOOKING_NOT_AVAILABLE));
    }

    @Test
    public void testUpdateExistentBooking() {
        final Booking expectedBooking = new Booking(Instant.now(), Instant.now(), Instant.now(), Instant.now());
        final BookingRequestDTO bookingRequestDTO = new BookingRequestDTO(NOW.plusDays(1),
                NOW.plusDays(2));

        when(bookingRepository.findById("id")).thenReturn(Optional.of(expectedBooking));

        bookingService.update("id", bookingRequestDTO);

        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    public void testUpdateNonExistentBooking() {
        final BookingRequestDTO bookingRequestDTO = new BookingRequestDTO(NOW.plusDays(1),
                NOW.plusDays(2));

        when(bookingRepository.findById("id")).thenReturn(Optional.empty());

        final BookingNotFoundException thrown = assertThrows(
                BookingNotFoundException.class,
                () -> bookingService.update("id", bookingRequestDTO),
                "Expected update() to throw BookingNotFoundException"
        );

        assertTrue(thrown.getMessage().contains(BOOKING_NOT_FOUND));
    }

}
