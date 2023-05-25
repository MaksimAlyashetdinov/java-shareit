package ru.practicum.shareit.booking;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoWithStatus;
import ru.practicum.shareit.booking.utils.BookingMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
public class BookingMapperTest {

    @Test
    void mapToBookingDtoTest() {
        User itemOwner = createUser(1);
        User booker = createUser(2);
        Item item = createItem(1, itemOwner.getId());
        Booking booking = createBooking(1, item, booker);
        BookingDto bookingDto = BookingMapper.mapToBookingDto(booking);
        assertEquals(bookingDto.getId(), 1L);
        assertEquals(bookingDto.getItemId(), item.getId());
        assertEquals(bookingDto.getBookerId(), booker.getId());
    }

    @Test
    void mapToBookingTest() {
        User itemOwner = createUser(1);
        User booker = createUser(2);
        Item item = createItem(1, itemOwner.getId());
        BookingDto bookingDto = createBookingDto(1, item, booker);
        Booking booking = BookingMapper.mapToBooking(bookingDto, item, booker, BookingState.WAITING);
        assertEquals(booking.getId(), 1L);
        assertEquals(booking.getItem().getId(), item.getId());
        assertEquals(booking.getBooker().getId(), booker.getId());
    }

    @Test
    void toBookingDtoWithStatusTest() {
        User itemOwner = createUser(1);
        User booker = createUser(2);
        Item item = createItem(1, itemOwner.getId());
        Booking booking = createBooking(1, item, booker);
        BookingDtoWithStatus bookingDtoWithStatus = BookingMapper.toBookingDtoWithStatus(booking);
        assertEquals(bookingDtoWithStatus.getId(), 1L);
        assertEquals(bookingDtoWithStatus.getItem().getId(), item.getId());
        assertEquals(bookingDtoWithStatus.getBooker().getId(), booker.getId());
    }

    private User createUser(long id) {
        User user = new User();
        user.setId(id);
        user.setName("User " + id);
        user.setEmail("user_" + id + "@email.ru");
        return user;
    }

    private Item createItem(long id, long ownerId) {
        Item item = new Item();
        item.setId(id);
        item.setName("Item " + id);
        item.setDescription("Description for item " + id);
        item.setOwnerId(ownerId);
        item.setAvailable(true);
        return item;
    }

    private Booking createBooking(long id, Item item, User booker) {
        Booking booking = new Booking();
        booking.setId(id);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(booking.getStart().plusDays(1));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingState.WAITING);
        return booking;
    }

    private BookingDto createBookingDto(long id, Item item, User booker) {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(id);
        bookingDto.setItemId(item.getId());
        bookingDto.setBookerId(booker.getId());
        return bookingDto;
    }
}