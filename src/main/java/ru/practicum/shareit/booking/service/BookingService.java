package ru.practicum.shareit.booking.service;

import java.util.List;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoWithStatus;

public interface BookingService {

    Booking create(long userId, BookingDto bookingDto);

    Booking getById(long id, long userId);

    Booking approveBooking(long id, boolean approved, long userId);

    List<BookingDtoWithStatus> getByBookerIdAndState(String state, long userId);

    List<BookingDtoWithStatus> getByItemOwnerIdAndState(String state, long userId);
}