package ru.practicum.shareit.booking.storage;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingState;

@Repository
public interface BookingStorage extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBookerId(long bookerId);

    List<Booking> findAllByItemOwnerId(long ownerId);

    List<Booking> findAllByBookerIdOrderByStartDesc(long userId);

    List<Booking> findAllByItemId(long itemId);

    @Query(value = "select * from bookings b " +
            "where b.start_booking < :now " +
            "and b.end_booking > :now " +
            "and b.booker_id = :userId " +
            "order by b.start_booking", nativeQuery = true)
    List<Booking> findByBookerCurrent(long userId, LocalDateTime now);

    @Query(value = "select * from bookings b " +
            "where b.end_booking < :end " +
            "and b.booker_id = :userId " +
            "order by b.start_booking desc", nativeQuery = true)
    List<Booking> findByBookerPast(long userId, LocalDateTime end);

    @Query(value = "select * from bookings b " +
            "where b.start_booking > :start " +
            "and b.booker_id = :userId " +
            "order by b.start_booking desc", nativeQuery = true)
    List<Booking> findByBookerFuture(long userId, LocalDateTime start);

    List<Booking> findByItemOwnerIdOrderByStartDesc(long ownerId);

    @Query(value = "select * from bookings b " +
            "join items as i on b.item_id = i.id " +
            "where b.start_booking < :now " +
            "and b.end_booking > :now " +
            "and i.owner_id = :userId " +
            "order by b.start_booking", nativeQuery = true)
    List<Booking> findByItemOwnerCurrent(long userId, LocalDateTime now);

    @Query(value = "select * from bookings b " +
            "join items as i on b.item_id = i.id " +
            "where b.end_booking < :end " +
            "and i.owner_id = :userId " +
            "order by b.start_booking desc", nativeQuery = true)
    List<Booking> findByItemOwnerPast(long userId, LocalDateTime end);

    @Query(value = "select * from bookings b " +
            "join items as i on b.item_id = i.id " +
            "where b.start_booking > :start " +
            "and i.owner_id = :userId " +
            "order by b.start_booking desc", nativeQuery = true)
    List<Booking> findByItemOwnerFuture(long userId, LocalDateTime start);

    List<Booking> findAllByItemIdAndStatus(Long itemId, BookingState status);

    @Query(value = "select * from bookings b " +
            "where b.item_id = :itemId and b.start_booking < :date order by b.start_booking limit  1",
            nativeQuery = true)
    Booking findBookingByItemWithDateBefore(long itemId, LocalDateTime date);
}