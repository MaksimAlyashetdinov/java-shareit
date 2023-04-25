package ru.practicum.shareit.booking.utils;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingDto;

@Component
public class BookingMapper {

    public BookingDto mapToBookingDto(Booking booking) {
        BookingDto dto = new BookingDto();
        dto.setItemId(booking.getItemId());
        dto.setStartBooking(booking.getStartBooking());
        dto.setEndBooking(booking.getEndBooking());
        return dto;
    }

    public Booking mapToBooking(BookingDto dto) {
        Booking booking = new Booking();
        booking.setItemId(dto.getItemId());
        booking.setStartBooking(dto.getStartBooking());
        booking.setEndBooking(dto.getEndBooking());
        return booking;
    }
}
