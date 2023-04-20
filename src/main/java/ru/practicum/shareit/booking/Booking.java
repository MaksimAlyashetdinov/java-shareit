package ru.practicum.shareit.booking;

import java.time.LocalDate;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 * TODO Sprint add-bookings.
 */

@Data
public class Booking {
    @NotNull
    private Long itemId;
    @NotNull
    private LocalDate startBooking;
    @NotNull
    private LocalDate endBooking;
}
