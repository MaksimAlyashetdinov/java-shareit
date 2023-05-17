package ru.practicum.shareit.booking.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoWithStatus;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.booking.utils.BookingMapper;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.storage.UserRepository;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingMapper bookingMapper;
    private static final Sort SORT = Sort.sort(Booking.class)
                                         .by(Booking::getStart)
                                         .descending();

    @Override
    public Booking create(long userId, BookingDto bookingDto) {
        User booker = userRepository.findById(userId)
                                    .orElseThrow(() -> new NotFoundException(
                                            "User with id = " + userId + " not exist."));
        Item item = containsItem(bookingDto.getItemId());
        if (userId == item.getOwnerId()) {
            throw new NotFoundException("You can't booking own items.");
        }
        Booking booking = bookingMapper.mapToBooking(bookingDto, item, booker,
                BookingState.WAITING);
        validateBooking(booking);
        checkItemState(booking, item);
        log.info("Booking successfully added: " + booking);
        return bookingRepository.save(booking);
    }

    @Override
    public Booking getById(long id, long userId) {
        containsUser(userId);
        Booking booking = bookingRepository.findById(id)
                                           .orElseThrow(() -> new NotFoundException(
                                                   "Booking not found."));
        Item item = containsItem(booking.getItem()
                                        .getId());
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
        Booking booking = bookingRepository.findById(id)
                                           .orElseThrow(() -> new NotFoundException(
                                                   "Booking not found."));
        Item item = containsItem(booking.getItem()
                                        .getId());
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
        return bookingRepository.save(booking);
    }

    @Override
    public List<BookingDtoWithStatus> getByBookerIdAndState(String state, long userId) {
        containsUser(userId);
        List<Booking> bookings = new ArrayList<>();
        switch (state) {
            case "ALL":
                bookings = bookingRepository.findByBookerIdOrderByStartDesc(userId);
                break;
            case "CURRENT":
                bookings = bookingRepository.findByBookerIdAndStartIsBeforeAndEndIsAfter(userId,
                        LocalDateTime.now(), LocalDateTime.now(), SORT);
                break;
            case "PAST":
                bookings = bookingRepository.findByBookerIdAndEndIsBefore(userId,
                        LocalDateTime.now(), SORT);
                break;
            case "FUTURE":
                bookings = bookingRepository.findByBookerIdAndStartIsAfter(userId,
                        LocalDateTime.now(), SORT);
                break;
            case "WAITING":
                bookings = bookingRepository.findByBookerId(userId)
                                            .stream()
                                            .filter(booking -> booking.getStatus()
                                                                      .equals(BookingState.WAITING))
                                            .collect(Collectors.toList());
                break;
            case "REJECTED":
                bookings = bookingRepository.findByBookerId(userId)
                                            .stream()
                                            .filter(booking -> booking.getStatus()
                                                                      .equals(BookingState.REJECTED))
                                            .collect(Collectors.toList());
                break;
            default:
                throw new ValidationException("Unknown state: " + state);
        }
        return bookings.stream()
                       .map(booking -> bookingMapper.toBookingDtoWithStatus(booking))
                       .collect(Collectors.toList());
    }

    @Override
    public List<BookingDtoWithStatus> getByItemOwnerIdAndState(String state, long userId) {
        containsUser(userId);
        List<Booking> bookings = new ArrayList<>();
        switch (state) {
            case "ALL":
                bookings = bookingRepository.findByItemOwnerIdOrderByStartDesc(userId);
                break;
            case "CURRENT":
                bookings = bookingRepository.findByItemOwnerIdAndStartIsBeforeAndEndIsAfter(userId,
                        LocalDateTime.now(), LocalDateTime.now(), SORT);
                break;
            case "PAST":
                bookings = bookingRepository.findByItemOwnerIdAndEndIsBefore(userId,
                        LocalDateTime.now(), SORT);
                break;
            case "FUTURE":
                bookings = bookingRepository.findByItemOwnerIdAndStartIsAfter(userId,
                        LocalDateTime.now(), SORT);
                break;
            case "WAITING":
                bookings = bookingRepository.findByItemOwnerId(userId)
                                            .stream()
                                            .filter(booking -> booking.getStatus()
                                                                      .equals(BookingState.WAITING))
                                            .collect(Collectors.toList());
                break;
            case "REJECTED":
                bookings = bookingRepository.findByItemOwnerId(userId)
                                            .stream()
                                            .filter(booking -> booking.getStatus()
                                                                      .equals(BookingState.REJECTED))
                                            .collect(Collectors.toList());
                break;
            default:
                throw new ValidationException("Unknown state: " + state);
        }
        return bookings.stream()
                       .map(booking -> bookingMapper.toBookingDtoWithStatus(booking))
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

    private void containsUser(long id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException("User not found.");
        }
    }

    private Item containsItem(long itemId) {
        if (itemId == 0) {
            throw new ValidationException("It is necessary to fill in all fields.");
        }
        Item item = itemRepository.findById(itemId)
                                  .orElseThrow(() -> new NotFoundException("Item not found."));
        if (!item.getAvailable()) {
            throw new ValidationException("Item is not available now.");
        }
        return item;
    }

    private void checkItemState(Booking booking, Item item) {
        Booking bookingFromRepository = bookingRepository.findByItemIdAndStatusAndStartIsBeforeAndEndIsAfter(
                item.getId(), BookingState.APPROVED, booking.getStart(), booking.getEnd());
        if (bookingFromRepository != null) {
            throw new ValidationException("Invalid booking range");
        }
    }
}