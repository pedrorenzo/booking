package com.pedrorenzo.booking.dtos;

import io.swagger.annotations.ApiModelProperty;

import java.time.LocalDate;

public class BookingResponseDTO {

    @ApiModelProperty(example = "608707209f974627a3ca5d70")
    private String id;

    @ApiModelProperty(example = "2021-04-26T00:00:00.000Z")
    private LocalDate fromDate;

    @ApiModelProperty(example = "2021-04-26T23:59:59.999Z")
    private LocalDate toDate;

    public BookingResponseDTO() {

    }

    public BookingResponseDTO(final String id, final LocalDate fromDate, final LocalDate toDate) {
        this.id = id;
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
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

}
