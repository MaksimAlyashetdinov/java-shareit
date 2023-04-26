package ru.practicum.shareit.booking.dto;

import java.time.LocalDate;
import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BookingDto {

    @NotNull
    private Long itemId;

    @NotNull
    private Long userId;

    @FutureOrPresent
    private LocalDate startBooking;

    @Future
    private LocalDate endBooking;
}
