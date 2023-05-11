package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class BookingDto {
    private Long id;
    private Long itemId;

    private Long bookerId;

    private LocalDateTime start;

    private LocalDateTime end;
}
