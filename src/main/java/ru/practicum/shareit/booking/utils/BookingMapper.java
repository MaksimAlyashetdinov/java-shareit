package ru.practicum.shareit.booking.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.storage.BookingStorage;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.storage.UserStorage;

@Component
@RequiredArgsConstructor
public class BookingMapper {

    private final ItemStorage itemStorage;
    private final UserStorage userStorage;
    private final BookingStorage bookingStorage;

    public BookingDto mapToBookingDto(Booking booking) {
        BookingDto dto = new BookingDto();
        dto.setId(booking.getId());
        dto.setItemId(booking.getItem().getId());
        dto.setBookerId(booking.getBooker().getId());
        dto.setStart(booking.getStart());
        dto.setEnd(booking.getEnd());
        return dto;
    }

    public Booking mapToBooking(BookingDto dto) {
        Booking booking = new Booking();
        booking.setId(dto.getId());
        Item item = itemStorage.findById(dto.getItemId()).orElseThrow(() -> new NotFoundException("Item not found."));
        booking.setItem(item);
        User booker = userStorage.findById(dto.getBookerId()).orElseThrow(() -> new NotFoundException("User not found."));
        booking.setBooker(booker);
        if (booking.getId() != null) {
            Booking bookingFromStorage = bookingStorage.findById(booking.getId()).orElseThrow(() -> new NotFoundException("Booking not found."));
            booking.setStatus(bookingFromStorage.getStatus());
        }
        booking.setStart(dto.getStart());
        booking.setEnd(dto.getEnd());
        return booking;
    }
}
