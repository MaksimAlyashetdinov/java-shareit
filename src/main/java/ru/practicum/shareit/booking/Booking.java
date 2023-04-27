package ru.practicum.shareit.booking;

import java.time.LocalDate;
import lombok.Data;

@Data
public class Booking {

    private Long itemId;
    private Long userId;
    private LocalDate startBooking;
    private LocalDate endBooking;
}
