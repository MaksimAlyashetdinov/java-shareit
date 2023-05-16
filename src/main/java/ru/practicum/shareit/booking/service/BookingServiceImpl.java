package ru.practicum.shareit.booking.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.dto.BookingDto;
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
    public Booking create(long userId, BookingDto bookingDto) {
        User booker = containsUser(userId);
        Item item = containsItem(bookingDto.getItemId());
        if (userId == item.getOwnerId()) {
            throw new NotFoundException("You can't booking own items.");
        }
        Booking booking = bookingMapper.mapToBooking(bookingDto, item, booker, BookingState.WAITING);
        validateBooking(booking);
        checkItemState(booking, item);
        log.info("Booking successfully added: " + booking);
        return bookingStorage.save(booking);
    }

    @Override
    public Booking getById(long id, long userId) {
        containsUser(userId);
        Booking booking = containsBooking(id);
        Item item = containsItem(booking.getItem().getId());
        if (booking.getBooker()
                   .getId() != userId && item.getOwnerId() != userId) {
            throw new NotFoundException(
                    "The user does not have access to the requested booking.");
        }
        log.info("Get booking " + booking);
        return booking;
    }

    @Override
    public Booking approveBooking(long id, boolean approved, long userId) {
        Booking booking = containsBooking(id);
        Item item = containsItem(booking.getItem().getId());
        containsUser(userId);
        if (item.getOwnerId() != userId) {
            throw new NotFoundException("This user can't change status.");
        }
        if (booking.getStatus()
                   .equals(BookingState.APPROVED)) {
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
    public List<BookingDtoToResponse> getByBookerIdAndState(String state, long userId) {
        containsUser(userId);
        List<Booking> bookings = new ArrayList<>();
        switch (state) {
            case "ALL":
                bookings = bookingStorage.findAllByBookerIdOrderByStartDesc(userId);
                break;
            case "CURRENT":
                bookings = bookingStorage.findByBookerCurrent(userId, LocalDateTime.now());
                break;
            case "PAST":
                bookings = bookingStorage.findByBookerPast(userId, LocalDateTime.now());
                break;
            case "FUTURE":
                bookings = bookingStorage.findByBookerFuture(userId, LocalDateTime.now());
                break;
            case "WAITING":
                bookings = bookingStorage.findAllByBookerId(userId)
                                         .stream()
                                         .filter(booking -> booking.getStatus()
                                                                   .equals(BookingState.WAITING))
                                         .collect(Collectors.toList());
                break;
            case "REJECTED":
                bookings = bookingStorage.findAllByBookerId(userId)
                                         .stream()
                                         .filter(booking -> booking.getStatus()
                                                                   .equals(BookingState.REJECTED))
                                         .collect(Collectors.toList());
                break;
            default:
                throw new ValidationException("Unknown state: " + state);
        }
        return bookings.stream()
                       .map(booking -> bookingMapper.toBookingDtoToResponse(booking))
                       .collect(Collectors.toList());
    }

    @Override
    public List<BookingDtoToResponse> getByItemOwnerIdAndState(String state, long userId) {
        containsUser(userId);
        List<Booking> bookings = new ArrayList<>();
        switch (state) {
            case "ALL":
                bookings = bookingStorage.findByItemOwnerIdOrderByStartDesc(userId);
                break;
            case "CURRENT":
                bookings = bookingStorage.findByItemOwnerCurrent(userId, LocalDateTime.now());
                break;
            case "PAST":
                bookings = bookingStorage.findByItemOwnerPast(userId, LocalDateTime.now());
                break;
            case "FUTURE":
                bookings = bookingStorage.findByItemOwnerFuture(userId, LocalDateTime.now());
                break;
            case "WAITING":
                bookings = bookingStorage.findAllByItemOwnerId(userId)
                                         .stream()
                                         .filter(booking -> booking.getStatus()
                                                                   .equals(BookingState.WAITING))
                                         .collect(Collectors.toList());
                break;
            case "REJECTED":
                bookings = bookingStorage.findAllByItemOwnerId(userId)
                                         .stream()
                                         .filter(booking -> booking.getStatus()
                                                                   .equals(BookingState.REJECTED))
                                         .collect(Collectors.toList());
                break;
            default:
                throw new ValidationException("Unknown state: " + state);
        }
        return bookings.stream()
                       .map(booking -> bookingMapper.toBookingDtoToResponse(booking))
                       .collect(Collectors.toList());
    }

    private void validateBooking(Booking booking) {
        if (booking.getStart() == null || booking.getStart()
                                                 .isBefore(LocalDateTime.now())) {
            throw new ValidationException(
                    "The start of the booking cannot be earlier than the current date or empty.");
        }
        if (booking.getEnd() == null || booking.getEnd()
                                               .isBefore(booking.getStart()) || booking.getEnd()
                                                                                       .isEqual(
                                                                                               booking.getStart())) {
            throw new ValidationException(
                    "The end of the booking cannot be earlier than the beginning or match it.");
        }
    }

    private User containsUser(long id) {
        return userStorage.findById(id)
                          .orElseThrow(() -> new NotFoundException(
                                  "User with id = " + id + " not exist."));
    }

    private Item containsItem(long itemId) {
        if (itemId == 0) {
            throw new ValidationException("It is necessary to fill in all fields.");
        }
        Item item = itemStorage.findById(itemId)
                               .orElseThrow(() -> new NotFoundException("Item not found."));
        if (!item.getAvailable()) {
            throw new ValidationException("Item is not available now.");
        }
        return item;
    }

    private Booking containsBooking(long id) {
        return bookingStorage.findById(id)
                             .orElseThrow(() -> new NotFoundException(
                                     "Booking with id = " + id + " not exist."));
    }

    private void checkItemState(Booking booking, Item item) {
        List<Booking> bookingsByItemId = bookingStorage.findAllByItemIdAndStatus(item.getId(),
                BookingState.APPROVED);
        for (Booking b : bookingsByItemId) {
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
    }
}