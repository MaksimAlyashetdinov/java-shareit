package ru.practicum.shareit.booking.storage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingState;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBookerIdOrderByStartDesc(long userId, Pageable pageable);

    List<Booking> findByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(long bookerId,
            LocalDateTime now,
            LocalDateTime today, Pageable pageable);

    List<Booking> findByBookerIdAndEndIsBeforeOrderByStartDesc(Long bookerId, LocalDateTime now,
            Pageable pageable);

    List<Booking> findByBookerIdAndStartIsAfterOrderByStartDesc(long bookerId, LocalDateTime now,
            Pageable pageable);

    List<Booking> findByItemOwnerIdOrderByStartDesc(long ownerId, Pageable pageable);

    List<Booking> findByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(long ownerId,
            LocalDateTime now,
            LocalDateTime today, Pageable pageable);

    List<Booking> findByItemOwnerIdAndEndIsBeforeOrderByStartDesc(long ownerId, LocalDateTime now,
            Pageable pageable);

    List<Booking> findByItemOwnerIdAndStartIsAfterOrderByStartDesc(long ownerId, LocalDateTime now,
            Pageable pageable);

    Booking findByItemIdAndStatusAndStartIsBeforeAndEndIsAfter(long itemId, BookingState status,
            LocalDateTime start, LocalDateTime end);

    Optional<Booking> findFirstByItemIdAndBookerIdAndStatusAndEndIsBefore(long itemId,
            long bookerId, BookingState status, LocalDateTime now);

    List<Booking> findByBookerIdAndStatusOrderByStartDesc(long bookerId, BookingState status,
            Pageable pageable);

    List<Booking> findByItemOwnerIdAndStatusOrderByStartDesc(long ownerId, BookingState status,
            Pageable pageable);

    Booking findFirstByItemIdAndStartIsBeforeAndStatusIsOrderByStartDesc(long itemId,
            LocalDateTime now, BookingState status);

    Booking findFirstByItemIdAndStartIsAfterAndStatusIsOrderByStartAsc(long itemId,
            LocalDateTime now, BookingState status);
}