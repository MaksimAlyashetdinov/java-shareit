package ru.practicum.shareit.item.utils;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoShort;
import ru.practicum.shareit.item.model.Item;

@Component
@RequiredArgsConstructor
public class ItemMapper {

    public ItemDto mapToItemDtoWithBookings(Item item, BookingDto lastBooking, BookingDto nextBooking, List<CommentDto> comments) {
        ItemDto dto = new ItemDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getAvailable());
        dto.setOwnerId(item.getOwnerId());
        dto.setLastBooking(lastBooking);
        dto.setNextBooking(nextBooking);
        if (comments != null) {
            dto.setComments(comments);
        } else {
            dto.setComments(new ArrayList<>());
        }
        return dto;
    }

    public ItemDto mapToItemDto(Item item,List<CommentDto> comments) {
        ItemDto dto = new ItemDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getAvailable());
        dto.setOwnerId(item.getOwnerId());
        if (comments != null) {
            dto.setComments(comments);
        } else {
            dto.setComments(new ArrayList<>());
        }
        return dto;
    }

    public ItemDtoShort toItemDtoShort(Item item) {
        return new ItemDtoShort(item.getId(), item.getName());
    }
}