package ru.practicum.shareit.booking.storage;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingState;

public interface BookingStorage extends JpaRepository<Booking, Long> {
    @Query
    List<Booking> findAllByBookerId(long bookerId);
    @Query
    List<Booking> findAllByItemOwnerId(long ownerId);

    List<Booking> findAllByItemIdAndStatus(Long itemId, BookingState status);
    @Query(value = "select * from bookings b " +
            "where b.item_id = :itemId and b.start_booking < :date order by b.start_booking limit  1",
            nativeQuery = true)
    Booking findBookingByItemWithDateBefore(long itemId, LocalDateTime date);

    @Query(value = "select * from bookings b " +
            "where b.item_id = :itemId and b.start_booking > :date order by b.start_booking limit  1",
            nativeQuery = true)
    Booking findBookingByItemWithDateAfter(long itemId, LocalDateTime date);
}
