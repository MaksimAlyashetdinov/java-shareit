package ru.practicum.shareit.booking.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.dto.BookingDtoToResponse;
import ru.practicum.shareit.booking.storage.BookingStorage;
import ru.practicum.shareit.booking.utils.BookingMapper;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.storage.UserStorage;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BookingServiceImpl implements BookingService {

    private final BookingStorage bookingStorage;
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;
    private final BookingMapper bookingMapper;

    @Override
    public Booking create(long userId, Booking booking) {
        User booker = containsUser(userId);
        Item item = containsItem(booking);
        if (userId == item.getOwnerId()) {
            throw new NotFoundException("You can't booking own items.");
        }
        validateBooking(booking);
        checkItemState(booking, item);
        booking.setBooker(booker);
        booking.setStatus(BookingState.WAITING);
        //booking.setCreateDate(LocalDateTime.now());
        log.info("Booking successfully added: " + booking);
        return bookingStorage.save(booking);
    }

    @Override
    public Booking getById(long id, long userId) {
        containsUser(userId);
        Booking booking = containsBooking(id);
        Item item = containsItem(booking);
        if (booking.getBooker().getId() != userId && item.getOwnerId() != userId) {
            throw new NotFoundException(
                    "The user does not have access to the requested booking.");
        }
        log.info("Get booking " + booking);
        return booking;
    }

    /*@Override
    public Booking update(Booking booking) {
        Booking bookingFromStorage = containsBooking(booking.getId());
        if (booking.getItem() != null) {
            bookingFromStorage.setItem(booking.getItem());
        }
        if (booking.getBooker() != null) {
            bookingFromStorage.setBooker(booking.getBooker());
        }
        if (booking.getStart() != null || booking.getEnd() != null) {
            checkItemState(booking, bookingFromStorage.getItem());
        }
        if (booking.getStart() != null) {
            bookingFromStorage.setStart(booking.getStart());
        }
        if (booking.getEnd() != null) {
            bookingFromStorage.setEnd(booking.getEnd());
        }
        if (booking.getStatus() != null) {
            bookingFromStorage.setStatus(booking.getStatus());
        }
        log.info("Booking successfully updated: " + bookingFromStorage);
        return bookingStorage.save(bookingFromStorage);
    }*/

    /*@Override
    public void delete(long id) {
        Booking booking = containsBooking(id);
        log.info("Deleted booking with id: {}", id);
        bookingStorage.delete(booking);
    }*/

    /*@Override
    public List<Booking> getAllByUserId(long id) {
        containsUser(id);
        return bookingStorage.findAllByBookerId(id);
    }*/

    /*@Override
    public List<Booking> getAllByOwnerId(long id) {
        containsUser(id);
        return bookingStorage.findAllByItemOwnerId(id);
    }*/

    @Override
    public Booking approveBooking(long id, boolean approved, long userId) {
        Booking booking = containsBooking(id);
        containsUser(userId);
        if (booking.getItem().getOwnerId() != userId) {
            throw new NotFoundException("This user can't change status.");
        }
        if (booking.getStatus().equals(BookingState.APPROVED)) {
            throw new ValidationException("This booking is approved before that.");
        }
        if (approved == true) {
            booking.setStatus(BookingState.APPROVED);
        } else {
            booking.setStatus(BookingState.REJECTED);
        }
        log.info("Approve/rejected booking: " + booking);
        return bookingStorage.save(booking);
    }

    @Override
    public List<BookingDtoToResponse> getByStateAndUserId(String state, long userId) {
        containsUser(userId);
        List<Booking> bookingsByUserId = bookingStorage.findAllByBookerId(userId);
        return getBookingsByState(bookingsByUserId, state);
    }

    @Override
    public List<BookingDtoToResponse> getByItemOwnerIdAndState(String state, long userId) {
        containsUser(userId);
        List<Booking> bookingsByItemOwnerId = bookingStorage.findAllByItemOwnerId(userId);
        return getBookingsByState(bookingsByItemOwnerId, state);
    }

    private List<BookingDtoToResponse> getBookingsByState(List<Booking> bookings, String state) {
        List<Booking> resultBookings = new ArrayList<>();
        if (state == null || state.equals("ALL")) {
            resultBookings = bookings;
            resultBookings.sort(Comparator.comparing(Booking::getStart).reversed());
            return resultBookings.stream().map(booking -> bookingMapper.toBookingDtoToResponse(booking)).collect(
                    Collectors.toList());
        }
        switch (state) {
            case "CURRENT":
                for (Booking booking : bookings) {
                    if (booking.getStart().isBefore(LocalDateTime.now())
                            && booking.getEnd()
                                      .isAfter(LocalDateTime.now())) {
                        resultBookings.add(booking);
                    }
                }
                break;
            case "PAST":
                for (Booking booking : bookings) {
                    if (booking.getEnd().isBefore(LocalDateTime.now())) {
                        resultBookings.add(booking);
                    }
                }
                break;
            case "FUTURE":
                for (Booking booking : bookings) {
                    if (booking.getEnd().isAfter(LocalDateTime.now())) {
                        resultBookings.add(booking);
                    }
                }
                break;
            case "WAITING":
                for (Booking booking : bookings) {
                    if (booking.getStatus()
                               .equals(BookingState.WAITING)) {
                        resultBookings.add(booking);
                    }
                }
                break;
            case "REJECTED":
                for (Booking booking : bookings) {
                    if (booking.getStatus()
                               .equals(BookingState.REJECTED)) {
                        resultBookings.add(booking);
                    }
                }
                break;
            default: throw new ValidationException("Unknown state: " + state);
        }
        resultBookings.sort(Comparator.comparing(Booking::getStart).reversed());
        log.info("Get bookings by user id and state: " + resultBookings);
        return resultBookings.stream().map(booking -> bookingMapper.toBookingDtoToResponse(booking)).collect(
                Collectors.toList());
    }

    private void validateBooking(Booking booking) {
        if (booking.getStart() == null || booking.getStart().isBefore(LocalDateTime.now())) {
            throw new ValidationException(
                    "The start of the booking cannot be earlier than the current date or empty.");
        }
        if (booking.getEnd() == null || booking.getEnd()
                                               .isBefore(booking.getStart()) || booking.getEnd()
                .isEqual(booking.getStart())) {
            throw new ValidationException(
                    "The end of the booking cannot be earlier than the beginning or match it.");
        }
    }

    private User containsUser(long id) {
        User booker = userStorage.findById(id).orElseThrow(() -> new NotFoundException("User with id = " + id + " not exist."));
        return booker;
    }

    private Item containsItem(Booking booking) {
        if (booking.getItem() == null) {
            throw new ValidationException("It is necessary to fill in all fields.");
        }
        Item item = itemStorage.findById(booking.getItem().getId()).orElseThrow(() -> new NotFoundException("Item not found."));
        if (!item.getAvailable()) {
            throw new ValidationException("Item is not available now.");
        }
        return item;
    }

    private Booking containsBooking(long id) {
        Booking booking = bookingStorage.findById(id).orElseThrow(() -> new NotFoundException("Booking with id = " + id + " not exist."));
        return booking;
    }

    private void checkItemState(Booking booking, Item item) {
        List<Booking> bookingsByItem = bookingStorage.findAllByItemIdAndStatus(item.getId(),
                BookingState.APPROVED);
        for (Booking b : bookingsByItem) {
            if ((booking.getStart()
                        .isAfter(b.getStart()) && booking.getStart()
                                                         .isBefore(b.getEnd())) || (booking.getEnd()
                                                                                           .isAfter(
                                                                                                   b.getStart())
                    && booking.getEnd()
                              .isBefore(b.getEnd()))) {
                throw new ValidationException("Invalid booking range");
            }
        }
        //updateLastAndNextItemBooking(booking, item);
    }

   /* private void updateLastAndNextItemBooking(Booking booking, Item item) {
        if (item.getLastBooking() == null && booking.getStart()
                                                         .isBefore(LocalDateTime.now())) {
            item.setLastBooking(booking);
            itemStorage.save(item);
        }
        if (item.getNextBooking() == null && booking.getStart()
                                                       .isAfter(LocalDateTime.now())) {
            item.setNextBooking(booking);
            itemStorage.save(item);
        }
        if (booking.getStart()
                   .isBefore(LocalDateTime.now()) && booking.getStart()
                                                        .isAfter(item.getLastBooking().getStart())) {
            item.setLastBooking(booking);
            itemStorage.save(item);
        }
        if (booking.getStart()
                   .isAfter(LocalDateTime.now()) && booking.getStart()
                                                       .isBefore(item.getNextBooking().getStart())) {
            item.setNextBooking(booking);
            itemStorage.save(item);
        }
    }*/
}
