package ru.practicum.shareit.item.utils;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

@Component
public class ItemMapper {

    public static ItemDto mapToItemDtoWithBookings(Item item, BookingDto lastBooking,
            BookingDto nextBooking, List<CommentDto> comments) {
        if (item.getId() == null || item.getName() == null || item.getDescription() == null || item.getAvailable() == null || item.getOwnerId() == null) {
            throw new ValidationException("All item fields must be filled in.");
        }
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
        if (item.getId() == null || item.getName() == null || item.getDescription() == null || item.getAvailable() == null || item.getOwnerId() == null) {
            throw new ValidationException("All item fields must be filled in.");
        }
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

    public static Item mapToItem(ItemDto itemDto) {
        if (itemDto.getId() == null || itemDto.getName() == null || itemDto.getDescription() == null || itemDto.getAvailable() == null || itemDto.getOwnerId() == null) {
            throw new ValidationException("All itemDto fields must be filled in.");
        }
        Item item = new Item();
        item.setId(itemDto.getId());
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setOwnerId(itemDto.getOwnerId());
        item.setAvailable(itemDto.getAvailable());
        item.setRequestId(itemDto.getRequestId());
        return item;
    }
}