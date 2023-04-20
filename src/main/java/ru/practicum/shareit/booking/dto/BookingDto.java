package ru.practicum.shareit.booking.dto;

import java.time.LocalDate;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 * TODO Sprint add-bookings.
 */

@Data
public class BookingDto {
    private Long itemId;
    private LocalDate startBooking;
    private LocalDate endBooking;
}
