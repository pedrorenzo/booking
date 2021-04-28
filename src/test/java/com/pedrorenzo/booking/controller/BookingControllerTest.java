package com.pedrorenzo.booking.controller;

import com.pedrorenzo.booking.controllers.BookingController;
import com.pedrorenzo.booking.dtos.BookingRequestDTO;
import com.pedrorenzo.booking.dtos.BookingResponseDTO;
import com.pedrorenzo.booking.exceptions.BookingNotFoundException;
import com.pedrorenzo.booking.exceptions.InvalidBookingException;
import com.pedrorenzo.booking.services.impl.BookingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.time.Month;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Collections;

import static com.pedrorenzo.booking.utils.ErrorMessages.BOOKING_NOT_FOUND;
import static com.pedrorenzo.booking.utils.ErrorMessages.STAY_START_INVALID;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
public class BookingControllerTest {

    @Autowired
    private WebApplicationContext context;

    @MockBean
    private BookingServiceImpl bookingService;

    private static final LocalDate NOW = OffsetDateTime.now(ZoneOffset.UTC).toLocalDate();

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    protected MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(this.context)
                .build();
    }

    @Test
    public void testGetBookingById() throws Exception {
        when(bookingService.findById("id"))
                .thenReturn(new BookingResponseDTO("id", LocalDate.of(2020, Month.JANUARY, 8),
                        LocalDate.of(2020, Month.JANUARY, 9)));

        this.mockMvc
                .perform(get("/v1/bookings/id"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value("id"))
                .andExpect(jsonPath("$.data.fromDate").value("2020-01-08"))
                .andExpect(jsonPath("$.data.toDate").value("2020-01-09"))
                .andExpect(jsonPath("$.errors.size()").value(0));
    }

    @Test
    public void testGetBookingByNonExistentId() throws Exception {
        when(bookingService.findById("id")).thenThrow(new BookingNotFoundException(BOOKING_NOT_FOUND));

        this.mockMvc
                .perform(get("/v1/bookings/id"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.data.size()").isEmpty())
                .andExpect(jsonPath("$.errors[0]").value(
                        BOOKING_NOT_FOUND));
    }

    @Test
    public void testGetAllBookings() throws Exception {
        when(bookingService.findAll()).thenReturn(Collections.singletonList(
                new BookingResponseDTO("id", LocalDate.of(2020, Month.JANUARY, 8),
                        LocalDate.of(2020, Month.JANUARY, 9))));

        this.mockMvc
                .perform(get("/v1/bookings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").value("id"))
                .andExpect(jsonPath("$.data[0].fromDate").value(
                        "2020-01-08"))
                .andExpect(jsonPath("$.data[0].toDate").value("2020-01-09"))
                .andExpect(jsonPath("$.errors.size()").value(0));
    }

    @Test
    public void testDeleteBookingById() throws Exception {
        doNothing().when(bookingService).deleteById(any(String.class));

        this.mockMvc
                .perform(delete("/v1/bookings/id"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testPostBooking() throws Exception {
        when(bookingService.insert(any())).thenReturn(new BookingResponseDTO("id",
                NOW.plusDays(1),
                NOW.plusDays(2)));

        this.mockMvc
                .perform(post("/v1/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fromDate\": \"" + NOW.plusDays(1).format(DATE_TIME_FORMATTER)
                                + "\", \"toDate\": \"" + NOW.plusDays(2).format(DATE_TIME_FORMATTER) + "\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").value("id"))
                .andExpect(jsonPath("$.data.fromDate").value(
                        NOW.plusDays(1).toString()))
                .andExpect(jsonPath("$.data.toDate").value(NOW.plusDays(2).toString()))
                .andExpect(jsonPath("$.errors.size()").value(0));
    }

    @Test
    public void testPostBookingWithInvalidData() throws Exception {
        when(bookingService.insert(any())).thenThrow(new InvalidBookingException(STAY_START_INVALID));

        this.mockMvc
                .perform(post("/v1/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fromDate\": \"" + NOW.format(DATE_TIME_FORMATTER)
                                + "\", \"toDate\": \"" + NOW.format(DATE_TIME_FORMATTER) + "\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data.size()").isEmpty())
                .andExpect(jsonPath("$.errors[0]").value(STAY_START_INVALID));
    }

    @Test
    public void testPutBooking() throws Exception {
        doNothing().when(bookingService).update(any(String.class), any(BookingRequestDTO.class));

        this.mockMvc
                .perform(put("/v1/bookings/id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fromDate\": \"" + NOW.plusDays(1).format(DATE_TIME_FORMATTER)
                                + "\", \"toDate\": \"" + NOW.plusDays(2).format(DATE_TIME_FORMATTER) + "\"}"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testPutInvalidBookingBody() throws Exception {
        this.mockMvc
                .perform(put("/v1/bookings/id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fromDate\": \"" + NOW.plusDays(1).format(DATE_TIME_FORMATTER) + "\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testPostInvalidBookingBody() throws Exception {
        this.mockMvc
                .perform(put("/v1/bookings/id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"toDate\": \"" + NOW.plusDays(1).format(DATE_TIME_FORMATTER) + "\"}"))
                .andExpect(status().isBadRequest());
    }

}