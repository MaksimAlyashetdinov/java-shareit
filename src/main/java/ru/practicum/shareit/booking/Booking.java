package ru.practicum.shareit.booking;

import java.time.LocalDate;
import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class Booking {

    @NotNull
    private Long itemId;
    @NotNull
    private Long userId;
    @NotNull
    @FutureOrPresent
    private LocalDate startBooking;
    @NotNull
    @Future
    private LocalDate endBooking;
}
