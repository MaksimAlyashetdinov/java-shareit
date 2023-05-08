package ru.practicum.shareit.booking.service;

import java.util.List;
import ru.practicum.shareit.booking.Booking;

public interface BookingService {
    Booking create(long userId, Booking booking);
    Booking getById(long id, long userId);
    Booking update(Booking booking);
    void delete(long id);
    List<Booking> getAllByUserId(long id);
    List<Booking> getAllByOwnerId(long id);
    Booking changeStateOfBooking(long id, boolean approved);

    List<Booking> getByStateAndUserId(String state, long userId);

    List<Booking> getByItemOwnerIdAndState(String state, long userId);
}
