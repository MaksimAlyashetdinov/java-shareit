package ru.practicum.shareit.booking.service;

import java.util.List;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingDtoToResponse;

public interface BookingService {
    Booking create(long userId, Booking booking);
    Booking getById(long id, long userId);
    //Booking update(Booking booking);
    //void delete(long id);
    //List<Booking> getAllByUserId(long id);
    //List<Booking> getAllByOwnerId(long id);
    Booking approveBooking(long id, boolean approved, long userId);

    List<BookingDtoToResponse> getByStateAndUserId(String state, long userId);

    List<BookingDtoToResponse> getByItemOwnerIdAndState(String state, long userId);
}
