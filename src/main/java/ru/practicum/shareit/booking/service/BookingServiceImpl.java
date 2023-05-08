package ru.practicum.shareit.booking.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.storage.BookingStorage;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.storage.UserStorage;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BookingServiceImpl implements BookingService {

    private final BookingStorage bookingStorage;
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Override
    public Booking create(long userId, Booking booking) {
        containsUser(userId);
        validateBooking(booking);
        containsItem(booking.getItem().getId());
        checkItemState(booking, booking.getItem().getId());
        booking.setUserId(userId);
        booking.setState(BookingState.WAITING);
        booking.setCreateDate(LocalDate.now());
        log.info("Booking successfully added: " + booking);
        return bookingStorage.save(booking);
    }

    @Override
    public Booking getById(long id, long userId) {
        containsUser(userId);
        Booking booking = bookingStorage.findById(id).orElseThrow(() -> new NotFoundException("Booking not found."));
        if (booking.getUserId() != userId || booking.getItem().getOwnerId() != userId) {
            throw new ValidationException("The user does not have access to the requested booking.");
        }
        log.info("Requested booking with ID = " + id);
        return booking;
    }

    @Override
    public Booking update(Booking booking) {
        Booking bookingFromStorage = bookingStorage.findById(booking.getId()).orElseThrow(() -> new NotFoundException("Booking not found."));
        if (booking.getItem() != null) {
            bookingFromStorage.setItem(booking.getItem());
        }
        if (booking.getUserId() != null) {
            bookingFromStorage.setUserId(booking.getUserId());
        }
        if (booking.getStartBooking() != null || booking.getEndBooking() != null) {
            checkItemState(booking, booking.getItem().getId());
        }
        if (booking.getStartBooking() != null) {
            bookingFromStorage.setStartBooking(booking.getStartBooking());
        }
        if (booking.getEndBooking() != null) {
            bookingFromStorage.setEndBooking(booking.getEndBooking());
        }
        if (booking.getState() != null) {
            bookingFromStorage.setState(booking.getState());
        }
        log.info("Booking successfully updated: " + bookingFromStorage);
        return bookingStorage.save(bookingFromStorage);
    }

    @Override
    public void delete(long id) {
        Booking booking = bookingStorage.findById(id).orElseThrow(() -> new NotFoundException("Booking not found."));
        log.info("Deleted booking with id: {}", id);
        bookingStorage.delete(booking);
    }

    @Override
    public List<Booking> getAllByUserId(long id) {
        containsUser(id);
        return bookingStorage.findAllByUserId(id);
    }

    @Override
    public List<Booking> getAllByOwnerId(long id) {
        containsUser(id);
        return bookingStorage.findAllByItemOwnerId(id);
    }

    @Override
    public Booking changeStateOfBooking(long id, boolean approved) {
        Booking booking = bookingStorage.findById(id).orElseThrow(() -> new NotFoundException("Booking not found."));
        if (approved == true) {
            booking.setState(BookingState.APPROVED);
        } else {
            booking.setState(BookingState.REJECTED);
        }
        return bookingStorage.save(booking);
    }

    @Override
    public List<Booking> getByStateAndUserId(String state, long userId) {
        containsUser(userId);
        List<Booking> bookingsByUserId = bookingStorage.findAllByUserId(userId);
        return getBookingsByState(bookingsByUserId, state);
    }

    @Override
    public List<Booking> getByItemOwnerIdAndState(String state, long userId) {
        containsUser(userId);
        List<Booking> bookingsByItemOwnerId = bookingStorage.findAllByItemOwnerId(userId);
        return getBookingsByState(bookingsByItemOwnerId, state);
    }

    private List<Booking> getBookingsByState(List<Booking> bookings, String state) {
        if (state == null || state.equals("ALL")) {
            Collections.sort(bookings, (b1, b2) -> b1.getCreateDate()
                                                     .compareTo(b2.getCreateDate()));
            return bookings;
        }
        if (state.equals("CURRENT")) {
            List<Booking> currentBookings = new ArrayList<>();
            for (Booking booking : bookings) {
                if ((booking.getState()
                            .equals(BookingState.APPROVED)) && (booking.getStartBooking()
                                                                       .isBefore(LocalDate.now())
                        && booking.getEndBooking()
                                  .isAfter(LocalDate.now()))) {
                    currentBookings.add(booking);
                }
            }
            Collections.sort(currentBookings, (b1, b2) -> b1.getCreateDate()
                                                            .compareTo(b2.getCreateDate()));
            return currentBookings;
        }
        if (state.equals("**PAST**")) {
            List<Booking> pastBookings = new ArrayList<>();
            for (Booking booking : bookings) {
                if (booking.getState()
                           .equals(BookingState.APPROVED) && booking.getEndBooking()
                                                                    .isBefore(LocalDate.now())) {
                    pastBookings.add(booking);
                }
            }
            Collections.sort(pastBookings, (b1, b2) -> b1.getCreateDate()
                                                         .compareTo(b2.getCreateDate()));
            return pastBookings;
        }
        if (state.equals("FUTURE")) {
            List<Booking> futureBookings = new ArrayList<>();
            for (Booking booking : bookings) {
                if (booking.getState()
                           .equals(BookingState.APPROVED) && booking.getStartBooking()
                                                                    .isAfter(LocalDate.now())) {
                    futureBookings.add(booking);
                }
            }
            Collections.sort(futureBookings, (b1, b2) -> b1.getCreateDate()
                                                           .compareTo(b2.getCreateDate()));
            return futureBookings;
        }
        if (state.equals("WAITING")) {
            List<Booking> waitingBookings = new ArrayList<>();
            for (Booking booking : bookings) {
                if (booking.getState()
                           .equals(BookingState.WAITING)) {
                    waitingBookings.add(booking);
                }
            }
            Collections.sort(waitingBookings, (b1, b2) -> b1.getCreateDate()
                                                            .compareTo(b2.getCreateDate()));
            return waitingBookings;
        }
        if (state.equals("REJECTED")) {
            List<Booking> rejectedBookings = new ArrayList<>();
            for (Booking booking : bookings) {
                if (booking.getState()
                           .equals(BookingState.REJECTED)) {
                    rejectedBookings.add(booking);
                }
            }
            Collections.sort(rejectedBookings, (b1, b2) -> b1.getCreateDate()
                                                             .compareTo(b2.getCreateDate()));
            return rejectedBookings;
        }
        return null;
    }

    private void validateBooking(Booking booking) {
        if (booking.getItem() == null || booking.getStartBooking() == null || booking.getEndBooking() == null) {
            throw new ValidationException("It is necessary to fill in all fields.");
        }
        if (booking.getStartBooking().isBefore(LocalDate.now())) {
            throw new ValidationException("The start of the booking cannot be earlier than the current date.");
        }
        if (booking.getEndBooking().isBefore(booking.getStartBooking()) || booking.getEndBooking().isEqual(booking.getStartBooking())) {
            throw new ValidationException("The end of the booking cannot be earlier than the beginning or match it.");
        }
    }

    private void containsUser(long id) {
        if (userStorage.findById(id).isEmpty()) {
            throw new NotFoundException("User with id = " + id + " not exist.");
        }
    }

    private void containsItem(long id) {
        if (itemStorage.findById(id).isEmpty()) {
            throw new NotFoundException("Item with id = " + id + " not exist.");
        }
    }

    private void checkItemState(Booking booking, long itemId) {
        List<Booking> bookingsByItem = bookingStorage.findAllByItemIdAndState(booking.getItem().getId(), BookingState.APPROVED.toString());
        for (Booking b : bookingsByItem) {
            if ((booking.getStartBooking().isAfter(b.getStartBooking()) && booking.getStartBooking().isBefore(b.getEndBooking())) || (booking.getEndBooking().isAfter(b.getStartBooking()) && booking.getEndBooking().isBefore(b.getEndBooking()))) {
                throw new ValidationException("Invalid booking range");
            }
        }
        updateLastAndNextItemBooking(booking, itemId);
    }

    private void updateLastAndNextItemBooking(Booking booking, long itemId) {
        Item item = itemStorage.findById(itemId).orElseThrow(() -> new NotFoundException("Item not found."));
        if (item.getLastStartBooking() == null && booking.getStartBooking().isBefore(LocalDate.now())) {
            item.setLastStartBooking(booking.getStartBooking());
            item.setLastEndBooking(booking.getEndBooking());
            itemStorage.save(item);
        }
        if (item.getNextEndBooking() == null && booking.getStartBooking().isAfter(LocalDate.now())) {
            item.setNextStartBooking(booking.getStartBooking());
            item.setNextEndBooking(booking.getEndBooking());
            itemStorage.save(item);
        }
        if (booking.getStartBooking().isBefore(LocalDate.now()) && booking.getStartBooking().isAfter(item.getLastStartBooking())) {
            item.setLastStartBooking(booking.getStartBooking());
            item.setLastEndBooking(booking.getEndBooking());
            itemStorage.save(item);
        }
        if (booking.getStartBooking().isAfter(LocalDate.now()) && booking.getStartBooking().isBefore(item.getNextStartBooking())) {
            item.setNextStartBooking(booking.getStartBooking());
            item.setNextEndBooking(booking.getEndBooking());
            itemStorage.save(item);
        }
    }
}
