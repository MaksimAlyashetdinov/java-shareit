package ru.practicum.shareit.item.utils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.storage.BookingStorage;
import ru.practicum.shareit.booking.utils.BookingMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoShort;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentRepository;

@Component
@RequiredArgsConstructor
public class ItemMapper {
    private final BookingStorage bookingStorage;
    private final CommentRepository commentRepository;
    private final BookingMapper bookingMapper;
    private final CommentMapper commentMapper;

    public ItemDto mapToItemDtoWithBookings(Item item) {
        ItemDto dto = new ItemDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getAvailable());
        dto.setOwnerId(item.getOwnerId());
        dto.setLastBooking(null);
        dto.setNextBooking(null);
        List<Booking> bookingsBefore = bookingStorage.bookingsBefore(item.getId(), LocalDateTime.now());
        bookingsBefore.removeIf(booking -> booking.getStatus().equals(BookingState.REJECTED));
        Booking lastBooking = null;
        if (bookingsBefore.size() > 0) {
            lastBooking = bookingsBefore.get(0);
            for (Booking b : bookingsBefore) {
                if (b.getStart().isAfter(lastBooking.getStart())) {
                    lastBooking = b;
                    break;
                }
            }
        }
        if (lastBooking == null) {
            dto.setLastBooking(null);
        } else {
            dto.setLastBooking(bookingMapper.mapToBookingDto(lastBooking));
        }
        List<Booking> bookingsAfter = bookingStorage.bookingsAfter(item.getId(), LocalDateTime.now());
        Booking nextBooking = null;
        if (bookingsAfter.size() > 0) {
            for (Booking b : bookingsAfter) {
                if (b.getStatus()
                     .equals(BookingState.APPROVED) || b.getStatus()
                                                        .equals(BookingState.WAITING)) {
                    nextBooking = b;
                    break;
                }
            }
        }
        if (nextBooking == null) {
            dto.setNextBooking(null);
        } else {
            dto.setNextBooking(bookingMapper.mapToBookingDto(nextBooking));
        }
        /*Booking lastBooking = bookingStorage.findBookingByItemWithDateBefore(item.getId(), LocalDateTime.now());
        if (lastBooking != null) {
            dto.setLastBooking(bookingMapper.mapToBookingDto(lastBooking));
        }
        Booking nextBooking = bookingStorage.findBookingByItemWithDateAfter(item.getId(), LocalDateTime.now(), BookingState.APPROVED, BookingState.WAITING);
        if (nextBooking != null) {
            dto.setNextBooking(bookingMapper.mapToBookingDto(nextBooking));
        }*/
        List<Comment> comments = commentRepository.findAllByItemId(item.getId());
        if (comments != null) {
            dto.setComments(comments.stream().map(comment -> commentMapper.toCommentDto(comment)).collect(
                    Collectors.toList()));
        } else {
            dto.setComments(new ArrayList<>());
        }
        System.out.println("All bookings for item " + item.getId() + " LAST bookings: " + bookingsBefore + " NEXT bookings: " + bookingsAfter);
        return dto;
    }

    public ItemDto mapToItemDto(Item item) {
        ItemDto dto = new ItemDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getAvailable());
        dto.setOwnerId(item.getOwnerId());
        List<Comment> comments = commentRepository.findAllByItemId(item.getId());
        if (comments != null) {
            dto.setComments(comments.stream().map(comment -> commentMapper.toCommentDto(comment)).collect(Collectors.toList()));
        } else {
            dto.setComments(new ArrayList<>());
        }
        return dto;
    }

    public ItemDtoShort toItemDtoShort(Item item) {
        return new ItemDtoShort(item.getId(), item.getName());
    }
}