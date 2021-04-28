package com.pedrorenzo.booking.utils;

import static com.pedrorenzo.booking.utils.Constants.*;

public class ErrorMessages {

    public static final String BOOKING_NOT_FOUND  = "Booking not found.";

    public static final String FROM_DATE_AFTER_TO_DATE  =
            "The from date parameter should be before the to date parameter.";

    public static final String STAY_START_INVALID = "The reservation must start at least the next day of booking.";

    public static final String STAY_LONGER_THAN_LIMIT  =
            "The stay can't be longer than " + (MAXIMUM_DIFF_STAY_DAYS + 1) + " days.";

    public static final String DAYS_IN_ADVANCE_LONGER_THAN_LIMIT  =
            "The reservation can't be made more than " + MAXIMUM_DIFF_DAYS_IN_ADVANCE + " days in advance.";

    public static final String BOOKING_NOT_AVAILABLE = "Unfortunately there is already a booking on this date.";

}
