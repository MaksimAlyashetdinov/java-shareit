package ru.practicum.shareit.booking.storage;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingState;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBookerId(long bookerId);

    List<Booking> findByItemOwnerId(long ownerId);

    List<Booking> findByBookerIdOrderByStartDesc(long userId);

    List<Booking> findByItemId(long itemId);

    List<Booking> findByBookerIdAndStartIsBeforeAndEndIsAfter(long bookerId, LocalDateTime now,
            LocalDateTime today, Sort sort);

    List<Booking> findByBookerIdAndEndIsBefore(Long bookerId, LocalDateTime now, Sort sort);

    List<Booking> findByBookerIdAndStartIsAfter(long bookerId, LocalDateTime now, Sort sort);

    List<Booking> findByItemOwnerIdOrderByStartDesc(long ownerId);

    List<Booking> findByItemOwnerIdAndStartIsBeforeAndEndIsAfter(long ownerId, LocalDateTime now,
            LocalDateTime today, Sort sort);

    List<Booking> findByItemOwnerIdAndEndIsBefore(long ownerId, LocalDateTime now, Sort sort);

    List<Booking> findByItemOwnerIdAndStartIsAfter(long ownerId, LocalDateTime now, Sort sort);

    Booking findByItemIdAndStatusAndStartIsBeforeAndEndIsAfter(long itemId, BookingState status,
            LocalDateTime start, LocalDateTime end);

    List<Booking> findByItemIdAndBookerIdAndStatusAndEndIsBefore(long itemId, long bookerId, BookingState status, LocalDateTime now);

    Booking findFirstByItemIdAndStartIsBeforeAndStatusIsOrderByStartDesc(long itemId, LocalDateTime now, BookingState status);

    Booking findFirstByItemIdAndStartIsAfterAndStatusIsOrderByStartAsc(long itemId, LocalDateTime now, BookingState status);
}