package ru.practicum.shareit.item.utils;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

@Component
public class ItemMapper {

    public static ItemDto mapToItemDtoWithBookings(Item item, BookingDto lastBooking,
            BookingDto nextBooking, List<CommentDto> comments) {
        ItemDto dto = new ItemDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getAvailable());
        dto.setOwnerId(item.getOwnerId());
        dto.setRequestId(item.getRequestId());
        dto.setLastBooking(lastBooking);
        dto.setNextBooking(nextBooking);
        if (comments != null) {
            dto.setComments(comments);
        } else {
            dto.setComments(new ArrayList<>());
        }
        return dto;
    }

    public static ItemDto mapToItemDto(Item item, List<CommentDto> comments) {
        ItemDto dto = new ItemDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getAvailable());
        dto.setOwnerId(item.getOwnerId());
        dto.setRequestId(item.getRequestId());
        if (comments != null) {
            dto.setComments(comments);
        } else {
            dto.setComments(new ArrayList<>());
        }
        return dto;
    }
}