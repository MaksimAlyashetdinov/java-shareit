package ru.practicum.shareit.booking.storage;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.Booking;

public interface BookingStorage extends JpaRepository<Booking, Long> {
    List<Booking> findAllByUserId(long id);
    List<Booking> findAllByItemOwnerId(long id);
    List<Booking> findAllByItemIdAndState(long id, String state);
}
