package ru.practicum.shareit.booking.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoWithStatus;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDtoShort;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

@Component
@RequiredArgsConstructor
public class BookingMapper {

    public static BookingDto mapToBookingDto(Booking booking) {
        if (booking.getId() == null || booking.getItem() == null || booking.getBooker() == null || booking.getStart() == null || booking.getEnd() == null) {
            throw  new ValidationException("All booking fields must be filled in.");
        }
        BookingDto dto = new BookingDto();
        dto.setId(booking.getId());
        dto.setItemId(booking.getItem().getId());
        dto.setBookerId(booking.getBooker().getId());
        dto.setStart(booking.getStart());
        dto.setEnd(booking.getEnd());
        return dto;
    }

    public static Booking mapToBooking(BookingDto dto, Item item, User booker, BookingState state) {
        if (dto.getStart() == null || dto.getEnd() == null) {
            throw  new ValidationException("All bookingDto fields must be filled in.");
        }
        if (item == null) {
            throw  new ValidationException("Item can't be empty.");
        }
        if (booker == null) {
            throw  new ValidationException("Booker can't be empty.");
        }

        if (state == null) {
            throw  new ValidationException("Booking state can't be empty.");
        }
        Booking booking = new Booking();
        booking.setId(dto.getId());
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(state);
        booking.setStart(dto.getStart());
        booking.setEnd(dto.getEnd());
        return booking;
    }

    public static BookingDtoWithStatus toBookingDtoWithStatus(Booking booking) {
        if (booking.getId() == null || booking.getItem() == null || booking.getBooker() == null || booking.getStart() == null || booking.getEnd() == null) {
            throw  new ValidationException("All booking fields must be filled in.");
        }
        BookingDtoWithStatus bookingDtoToResponse = new BookingDtoWithStatus();
        bookingDtoToResponse.setId(booking.getId());
        Item item = booking.getItem();
        bookingDtoToResponse.setItem(new ItemDtoShort(item.getId(), item.getName()));
        User booker = booking.getBooker();
        bookingDtoToResponse.setBooker(
                new UserDto(booker.getId(), booker.getName(), booker.getEmail()));
        bookingDtoToResponse.setStatus(booking.getStatus());
        bookingDtoToResponse.setStart(booking.getStart());
        bookingDtoToResponse.setEnd(booking.getEnd());
        return bookingDtoToResponse;
    }
}