package com.pedrorenzo.booking.dtos;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

public class BookingRequestDTO {

    @NotNull(message = "From date should not be blank.")
    @ApiModelProperty(example = "2021-04-26")
    private LocalDate fromDate;

    @NotNull(message = "To date should not be blank.")
    @ApiModelProperty(example = "2021-04-28")
    private LocalDate toDate;

    public BookingRequestDTO() {

    }

    public BookingRequestDTO(final LocalDate fromDate, final LocalDate toDate) {
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    public LocalDate getFromDate() {
        return fromDate;
    }

    public void setFromDate(final LocalDate fromDate) {
        this.fromDate = fromDate;
    }

    public LocalDate getToDate() {
        return toDate;
    }

    public void setToDate(final LocalDate toDate) {
        this.toDate = toDate;
    }

    @Override
    public String toString() {
        return "BookingRequestDTO{" +
                "fromDate=" + fromDate +
                ", toDate=" + toDate +
                '}';
    }
}
