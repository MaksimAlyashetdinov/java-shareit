package ru.practicum.shareit.item.utils;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.storage.BookingStorage;
import ru.practicum.shareit.booking.utils.BookingMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentRepository;

@Component
@RequiredArgsConstructor
public class ItemMapper {
    private final BookingStorage bookingStorage;
    private final CommentRepository commentRepository;
    private final BookingMapper bookingMapper;

    public ItemDto mapToItemDtoWithBookings(Item item) {
        ItemDto dto = new ItemDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getAvailable());
        dto.setOwnerId(item.getOwnerId());
        Booking lastBooking = bookingStorage.findBookingByItemWithDateBefore(item.getId(), LocalDateTime.now());
        if (lastBooking != null) {
            dto.setLastBooking(bookingMapper.mapToBookingDto(lastBooking));
        } else {
            dto.setLastBooking(null);
        }
        Booking nextBooking = bookingStorage.findBookingByItemWithDateAfter(item.getId(), LocalDateTime.now());
        if (nextBooking != null) {
            dto.setNextBooking(bookingMapper.mapToBookingDto(nextBooking));
        } else {
            dto.setNextBooking(null);
        }
        dto.setComments(commentRepository.findAllByItemId(item.getId()));
        return dto;
    }

    public ItemDto mapToItemDto(Item item) {
        ItemDto dto = new ItemDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getAvailable());
        dto.setOwnerId(item.getOwnerId());
        return dto;
    }
}