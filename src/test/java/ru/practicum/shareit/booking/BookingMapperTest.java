package ru.practicum.shareit.booking;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoWithStatus;
import ru.practicum.shareit.booking.utils.BookingMapper;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
public class BookingMapperTest {

    private User itemOwner;
    private User booker;
    private Item item;
    private Booking booking;
    private BookingDto bookingDto;

    @BeforeEach
    void setUp() {
        itemOwner = createUser(1);
        booker = createUser(2);
        item = createItem(1, itemOwner.getId());
        booking = createBooking(1, item, booker);
        bookingDto = createBookingDto(1, item, booker);
    }

    @Test
    void mapToBookingDtoTest_Ok() {
        BookingDto bookingDto = BookingMapper.mapToBookingDto(booking);
        assertThat(bookingDto.getId()).isEqualTo(1L);
        assertThat(bookingDto.getItemId()).isEqualTo(item.getId());
        assertThat(bookingDto.getBookerId()).isEqualTo(booker.getId());
    }

    @Test
    void mapToBookingDtoTest_idIsNull() {
        booking.setId(null);
        ValidationException exception = assertThrows(
                ValidationException.class, () -> BookingMapper.mapToBookingDto(booking));
        assertThat("All booking fields must be filled in.").isEqualTo(exception.getMessage());
    }

    @Test
    void mapToBookingDtoTest_itemIsNull() {
        booking.setItem(null);
        ValidationException exception = assertThrows(
                ValidationException.class, () -> BookingMapper.mapToBookingDto(booking));
        assertThat("All booking fields must be filled in.").isEqualTo(exception.getMessage());
    }

    @Test
    void mapToBookingDtoTest_bookerIsNull() {
        booking.setBooker(null);
        ValidationException exception = assertThrows(
                ValidationException.class, () -> BookingMapper.mapToBookingDto(booking));
        assertThat("All booking fields must be filled in.").isEqualTo(exception.getMessage());
    }

    @Test
    void mapToBookingDtoTest_startIsNull() {
        booking.setStart(null);
        ValidationException exception = assertThrows(
                ValidationException.class, () -> BookingMapper.mapToBookingDto(booking));
        assertThat("All booking fields must be filled in.").isEqualTo(exception.getMessage());
    }

    @Test
    void mapToBookingDtoTest_endIsNull() {
        booking.setEnd(null);
        ValidationException exception = assertThrows(
                ValidationException.class, () -> BookingMapper.mapToBookingDto(booking));
        assertThat("All booking fields must be filled in.").isEqualTo(exception.getMessage());
    }

    @Test
    void mapToBookingTest_Ok() {
        Booking booking = BookingMapper.mapToBooking(bookingDto, item, booker,
                BookingState.WAITING);
        assertThat(booking.getId()).isEqualTo(1L);
        assertThat(booking.getItem().getId()).isEqualTo(item.getId());
        assertThat(booking.getBooker().getId()).isEqualTo(booker.getId());
    }

    @Test
    void mapToBookingTest_startIsNull() {
        bookingDto.setStart(null);
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> BookingMapper.mapToBooking(bookingDto, item, booker, BookingState.WAITING));
        assertThat("All bookingDto fields must be filled in.").isEqualTo(exception.getMessage());
    }

    @Test
    void mapToBookingTest_endIsNull() {
        bookingDto.setEnd(null);
        ValidationException exception = assertThrows(ValidationException.class,
                () -> BookingMapper.mapToBooking(bookingDto, item, booker, BookingState.WAITING));
        assertThat("All bookingDto fields must be filled in.").isEqualTo(exception.getMessage());
    }

    @Test
    void mapToBookingTest_itemIsNull() {
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> BookingMapper.mapToBooking(bookingDto, null, booker, BookingState.WAITING));
        assertThat("Item can't be empty.").isEqualTo(exception.getMessage());
    }

    @Test
    void mapToBookingTest_bookerIsNull() {
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> BookingMapper.mapToBooking(bookingDto, item, null, BookingState.WAITING));
        assertThat("Booker can't be empty.").isEqualTo(exception.getMessage());
    }

    @Test
    void mapToBookingTest_stateIsNull() {
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> BookingMapper.mapToBooking(bookingDto, item, booker, null));
        assertThat("Booking state can't be empty.").isEqualTo(exception.getMessage());
    }

    @Test
    void toBookingDtoWithStatusTest_Ok() {
        BookingDtoWithStatus bookingDtoWithStatus = BookingMapper.toBookingDtoWithStatus(booking);
        assertThat(bookingDtoWithStatus.getId()).isEqualTo(1L);
        assertThat(bookingDtoWithStatus.getItem().getId()).isEqualTo(item.getId());
        assertThat(bookingDtoWithStatus.getBooker().getId()).isEqualTo(booker.getId());
    }

    @Test
    void toBookingDtoWithStatusTest_idIsNull() {
        booking.setId(null);
        ValidationException exception = assertThrows(ValidationException.class,
                () -> BookingMapper.toBookingDtoWithStatus(booking));
        assertThat("All booking fields must be filled in.").isEqualTo(exception.getMessage());
    }

    @Test
    void toBookingDtoWithStatusTest_itemIsNull() {
        booking.setItem(null);
        ValidationException exception = assertThrows(ValidationException.class,
                () -> BookingMapper.toBookingDtoWithStatus(booking));
        assertThat("All booking fields must be filled in.").isEqualTo(exception.getMessage());
    }

    @Test
    void toBookingDtoWithStatusTest_bookerIsNull() {
        booking.setBooker(null);
        ValidationException exception = assertThrows(ValidationException.class,
                () -> BookingMapper.toBookingDtoWithStatus(booking));
        assertThat("All booking fields must be filled in.").isEqualTo(exception.getMessage());
    }

    @Test
    void toBookingDtoWithStatusTest_startIsNull() {
        booking.setStart(null);
        ValidationException exception = assertThrows(ValidationException.class,
                () -> BookingMapper.toBookingDtoWithStatus(booking));
        assertThat("All booking fields must be filled in.").isEqualTo(exception.getMessage());
    }

    @Test
    void toBookingDtoWithStatusTest_endIsNull() {
        booking.setEnd(null);
        ValidationException exception = assertThrows(ValidationException.class,
                () -> BookingMapper.toBookingDtoWithStatus(booking));
        assertThat("All booking fields must be filled in.").isEqualTo(exception.getMessage());
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
        booking.setStart(LocalDateTime.of(2023,12,1,8,0));
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
        bookingDto.setStart(LocalDateTime.of(2023, 12, 1, 8, 0));
        bookingDto.setEnd(LocalDateTime.of(2023, 12, 10, 8, 0));
        return bookingDto;
    }
}