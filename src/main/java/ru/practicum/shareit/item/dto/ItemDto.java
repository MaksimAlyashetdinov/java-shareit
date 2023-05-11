package ru.practicum.shareit.item.dto;

import java.util.List;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.model.Comment;

@Data
public class ItemDto {

    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long ownerId;
    private BookingDto lastBooking;
    private BookingDto nextBooking;
    private List<Comment> comments;
}
