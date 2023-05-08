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
import ru.practicum.shareit.booking.service.BookingService;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public Booking createBooking(@RequestHeader("X-Sharer-User-Id") long userId,
            @Valid @RequestBody Booking booking) {
        return bookingService.create(userId, booking);
    }

    @PatchMapping("/{bookingId}")
    public Booking changeStateOfBooking(@PathVariable long id, @RequestParam("approved") boolean approved) {
        return bookingService.changeStateOfBooking(id, approved);
    }

    @GetMapping("/{bookingId}")
    public Booking getById(@PathVariable long id, @RequestHeader("X-Sharer-User-Id") long userId) {
        return bookingService.getById(id, userId);
    }

    @GetMapping
    public List<Booking> getByStateAndUserId(@RequestParam("state") String state, @RequestHeader("X-Sharer-User-Id") long userId) {
        return bookingService.getByStateAndUserId(state, userId);
    }

    @GetMapping("/owner?state={state}")
    public List<Booking> getByItemOwnerIdAndState(@RequestParam("state") String state, @RequestHeader("X-Sharer-User-Id") long userId) {
        return bookingService.getByItemOwnerIdAndState(state, userId);
    }
}
