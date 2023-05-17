package ru.practicum.shareit.booking;

import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoWithStatus;
import ru.practicum.shareit.booking.service.BookingService;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final String HEADER = "X-Sharer-User-Id";

    private final BookingService bookingService;

    @PostMapping
    public Booking createBooking(@RequestHeader(HEADER) Long userId,
            @Valid @RequestBody BookingDto bookingDto) {
        bookingDto.setBookerId(userId);
        return bookingService.create(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public Booking changeStateOfBooking(@PathVariable Long bookingId,
            @RequestParam("approved") boolean approved,
            @RequestHeader(HEADER) Long userId) {
        return bookingService.approveBooking(bookingId, approved, userId);
    }

    @GetMapping("/{bookingId}")
    public Booking getById(@PathVariable Long bookingId,
            @RequestHeader(HEADER) Long userId) {
        return bookingService.getById(bookingId, userId);
    }

    @GetMapping
    public List<BookingDtoWithStatus> getByStateAndUserId(
            @RequestParam(value = "state", defaultValue = "ALL") String state,
            @RequestHeader(HEADER) Long userId) {
        return bookingService.getByBookerIdAndState(state, userId);
    }

    @GetMapping("/owner")
    public List<BookingDtoWithStatus> getByItemOwnerIdAndState(
            @RequestParam(value = "state", defaultValue = "ALL") String state,
            @RequestHeader(HEADER) Long userId) {
        return bookingService.getByItemOwnerIdAndState(state, userId);
    }
}