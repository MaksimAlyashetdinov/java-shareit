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
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.utils.BookingMapper;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private final BookingMapper bookingMapper;

    @PostMapping
    public Booking createBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
            @Valid @RequestBody BookingDto bookingDto) {
        bookingDto.setBookerId(userId);
        return bookingService.create(userId, bookingMapper.mapToBooking(bookingDto));
    }

    @PatchMapping("/{bookingId}")
    public Booking changeStateOfBooking(@PathVariable Long bookingId, @RequestParam("approved") boolean approved, @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.changeStateOfBooking(bookingId, approved, userId);
    }

    @GetMapping("/{bookingId}")
    public Booking getById(@PathVariable Long bookingId, @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.getById(bookingId, userId);
    }

    @GetMapping
    public List<Booking> getByStateAndUserId(@RequestParam(value = "state", required = false) String state, @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.getByStateAndUserId(state, userId);
    }

    @GetMapping("/owner")
    public List<Booking> getByItemOwnerIdAndState(@RequestParam(value = "state", required = false) String state, @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.getByItemOwnerIdAndState(state, userId);
    }
}
