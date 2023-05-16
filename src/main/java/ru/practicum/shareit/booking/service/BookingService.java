package ru.practicum.shareit.booking.service;

import java.util.List;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoToResponse;

public interface BookingService {

    Booking create(long userId, BookingDto bookingDto);

    Booking getById(long id, long userId);

    Booking approveBooking(long id, boolean approved, long userId);

    List<BookingDtoToResponse> getByBookerIdAndState(String state, long userId);

    List<BookingDtoToResponse> getByItemOwnerIdAndState(String state, long userId);
}