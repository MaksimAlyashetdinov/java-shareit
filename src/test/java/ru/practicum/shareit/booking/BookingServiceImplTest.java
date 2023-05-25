package ru.practicum.shareit.booking;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import ru.practicum.shareit.booking.dto.BookingDtoWithStatus;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.booking.utils.BookingMapper;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.storage.UserRepository;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @InjectMocks
    BookingServiceImpl bookingService;

    @Mock
    BookingRepository bookingRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    ItemRepository itemRepository;

    @Test
    void createBookingTest_UserNotFound() {
        User user = createUser(1);
        Item item = createItem(1, 2);
        Booking booking = createBooking(1, item, user);
        Mockito.when(userRepository.findById(1L))
               .thenReturn(Optional.of(new User()));
        Mockito.when(itemRepository.findById(Mockito.anyLong()))
               .thenReturn(Optional.of(item));
        Mockito.when(bookingRepository.save(any(Booking.class)))
               .thenReturn(new Booking());
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.create(2L,
                        BookingMapper.mapToBookingDto(booking)));
        assertEquals("User with id = 2 not exist.", exception.getMessage());
    }

    @Test
    void createBookingTest_ItemIdIsNull() {
        User user = createUser(1);
        Item item = createItem(0, 2);
        Booking booking = createBooking(1, item, user);
        Mockito.when(userRepository.findById(1L))
               .thenReturn(Optional.of(new User()));
        Mockito.when(itemRepository.findById(0L))
               .thenReturn(Optional.of(item));
        Mockito.when(bookingRepository.save(any(Booking.class)))
               .thenReturn(new Booking());
        ValidationException exception = assertThrows(ValidationException.class,
                () -> bookingService.create(1L, BookingMapper.mapToBookingDto(booking)));
        assertEquals("It is necessary to fill in all fields.", exception.getMessage());
    }

    @Test
    void createBookingTest_ItemNotFound() {
        User user = createUser(1);
        Item item = createItem(2, 2);
        Booking booking = createBooking(1, item, user);
        Mockito.when(userRepository.findById(1L))
               .thenReturn(Optional.of(new User()));
        Mockito.when(itemRepository.findById(1L))
               .thenReturn(Optional.of(item));
        Mockito.when(bookingRepository.save(any(Booking.class)))
               .thenReturn(new Booking());
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.create(1L, BookingMapper.mapToBookingDto(booking)));
        assertEquals("Item with id = 2 not exist.", exception.getMessage());
    }

    @Test
    void createBookingTest_NotAvailable() {
        User user = createUser(1);
        Item item = createItem(1, 2);
        item.setAvailable(false);
        Booking booking = createBooking(1, item, user);
        Mockito.when(userRepository.findById(1L))
               .thenReturn(Optional.of(new User()));
        Mockito.when(itemRepository.findById(Mockito.anyLong()))
               .thenReturn(Optional.of(item));
        Mockito.when(bookingRepository.save(any(Booking.class)))
               .thenReturn(new Booking());
        ValidationException exception = assertThrows(ValidationException.class,
                () -> bookingService.create(1L, BookingMapper.mapToBookingDto(booking)));
        assertEquals("Item is not available now.", exception.getMessage());
    }

    @Test
    void createBookingTest_BookingByOwner() {
        User user = createUser(1);
        Item item = createItem(1, 1);
        Booking booking = createBooking(1, item, user);
        Mockito.when(userRepository.findById(1L))
               .thenReturn(Optional.of(new User()));
        Mockito.when(itemRepository.findById(1L))
               .thenReturn(Optional.of(item));
        Mockito.when(bookingRepository.save(any(Booking.class)))
               .thenReturn(new Booking());
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.create(1L, BookingMapper.mapToBookingDto(booking)));
        assertEquals("You can't booking own items.", exception.getMessage());
    }

    @Test
    void createBookingTest_StartBeforeCurrentDate() {
        User user = createUser(1);
        Item item = createItem(1, 2);
        Booking booking = createBooking(1, item, user);
        booking.setStart(LocalDateTime.now().minusDays(1));
        Mockito.when(userRepository.findById(1L))
               .thenReturn(Optional.of(new User()));
        Mockito.when(itemRepository.findById(1L))
               .thenReturn(Optional.of(item));
        Mockito.when(bookingRepository.save(any(Booking.class)))
               .thenReturn(new Booking());
        ValidationException exception = assertThrows(ValidationException.class,
                () -> bookingService.create(1L, BookingMapper.mapToBookingDto(booking)));
        assertEquals("The start of the booking cannot be earlier than the current date or empty.",
                exception.getMessage());
    }

    @Test
    void createBookingTest_EndBeforeStartDate() {
        User user = createUser(1);
        Item item = createItem(1, 2);
        Booking booking = createBooking(1, item, user);
        booking.setEnd(booking.getStart().minusDays(1));
        Mockito.when(userRepository.findById(1L))
               .thenReturn(Optional.of(new User()));
        Mockito.when(itemRepository.findById(1L))
               .thenReturn(Optional.of(item));
        Mockito.when(bookingRepository.save(any(Booking.class)))
               .thenReturn(new Booking());
        ValidationException exception = assertThrows(ValidationException.class,
                () -> bookingService.create(1L, BookingMapper.mapToBookingDto(booking)));
        assertEquals("The end of the booking cannot be earlier than the beginning or match it.",
                exception.getMessage());
    }

    @Test
    void createBookingTest_Ok() {
        User user = createUser(1);
        Item item = createItem(1, 2);
        Booking booking = createBooking(1L, item, user);
        Mockito.when(userRepository.findById(1L))
               .thenReturn(Optional.of(new User()));
        Mockito.when(itemRepository.findById(1L))
               .thenReturn(Optional.of(item));
        Mockito.when(bookingRepository.save(Mockito.any(Booking.class)))
               .thenReturn(new Booking());
        Booking bookingFromService = bookingService.create(1L,
                BookingMapper.mapToBookingDto(booking));
        assertNotNull(bookingFromService);
    }

    @Test
    void getBookingByIdTest_BookingNotFound() {
        User user = createUser(1L);
        Item item = createItem(1, 2);
        Booking booking = createBooking(1, item, user);
        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(itemRepository.findById(1L))
               .thenReturn(Optional.of(item));
        Mockito.when(bookingRepository.save(booking)).thenReturn(new Booking());
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.getById(2L, user.getId()));
        assertEquals("Booking not found.", exception.getMessage());
    }

    @Test
    void getBookingByIdTest_NotBookerAndNotItemOwner() {
        User user = createUser(1L);
        Item item = createItem(1, 2);
        Booking booking = createBooking(1, item, user);
        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        Mockito.when(bookingRepository.save(booking)).thenReturn(booking);
        Mockito.when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.getById(1L, 3));
        assertEquals("The user does not have access to the requested booking.",
                exception.getMessage());
    }

    @Test
    void getBookingByIdTest_GetByBooker() {
        User user = createUser(1L);
        User itemOwner = createUser(2L);
        Item item = createItem(1, itemOwner.getId());
        Booking booking = createBooking(1, item, user);
        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        Mockito.when(bookingRepository.save(booking)).thenReturn(booking);
        Mockito.when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        Booking bookingFromService = bookingService.getById(1L, user.getId());
        assertNotNull(bookingFromService);
    }

    @Test
    void getBookingByIdTest_GetByItemOwner() {
        User user = createUser(1L);
        User itemOwner = createUser(2L);
        Item item = createItem(1, itemOwner.getId());
        Booking booking = createBooking(1, item, user);
        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        Mockito.when(bookingRepository.save(booking)).thenReturn(booking);
        Mockito.when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        Booking bookingFromService = bookingService.getById(1L, itemOwner.getId());
        assertNotNull(bookingFromService);
    }

    @Test
    void approveBookingTest_BookingNotFound() {
        User itemOwner = createUser(1);
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.approveBooking(1L, true, itemOwner.getId()));
        assertEquals("Booking not found.", exception.getMessage());
    }

    @Test
    void approveBookingTest_NotOwner() {
        User user = createUser(1L);
        User itemOwner = createUser(2);
        Item item = createItem(1, itemOwner.getId());
        Booking booking = createBooking(1, item, user);
        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        Mockito.when(bookingRepository.save(booking)).thenReturn(booking);
        Mockito.when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.approveBooking(1L, true, user.getId()));
        assertEquals("This user can't change status.", exception.getMessage());
    }

    @Test
    void approveBookingTest_AlreadyApprove() {
        User user = createUser(1L);
        User itemOwner = createUser(2);
        Item item = createItem(1, itemOwner.getId());
        Booking booking = createBooking(1, item, user);
        booking.setStatus(BookingState.APPROVED);
        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        Mockito.when(bookingRepository.save(booking)).thenReturn(booking);
        Mockito.when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        ValidationException exception = assertThrows(ValidationException.class,
                () -> bookingService.approveBooking(1L, true, itemOwner.getId()));
        assertEquals("This booking is approved before that.", exception.getMessage());
    }

    @Test
    void approveBookingTest_ApproveTrue() {
        User user = createUser(1L);
        User itemOwner = createUser(2);
        Item item = createItem(1, itemOwner.getId());
        Booking booking = createBooking(1, item, user);
        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        Mockito.when(bookingRepository.save(booking)).thenReturn(booking);
        Mockito.when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        Booking bookingFromService = bookingService.approveBooking(1L, true, itemOwner.getId());
        assertNotNull(bookingFromService);
        assertEquals(BookingState.APPROVED, bookingFromService.getStatus());
    }

    @Test
    void approveBookingTest_ApproveFalse() {
        User user = createUser(1L);
        User itemOwner = createUser(2);
        Item item = createItem(1, itemOwner.getId());
        Booking booking = createBooking(1, item, user);
        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        Mockito.when(bookingRepository.save(booking)).thenReturn(booking);
        Mockito.when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        Booking bookingFromService = bookingService.approveBooking(1L, false, itemOwner.getId());
        assertNotNull(bookingFromService);
        assertEquals(BookingState.REJECTED, bookingFromService.getStatus());
    }

    @Test
    void getByBookerIdAndStateTest_PageFromLessThanZero() {
        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
        ValidationException exception = assertThrows(ValidationException.class,
                () -> bookingService.getByBookerIdAndState("ALL", 1, -1,10));
        assertEquals("It is not possible to start the display with a negative element.",
                exception.getMessage());
    }

    @Test
    void getByBookerIdAndStateTest_PageSizeLessThanOne() {
        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
        ValidationException exception = assertThrows(ValidationException.class,
                () -> bookingService.getByBookerIdAndState("ALL", 1, 0,0));
        assertEquals("The number of records cannot be less than 1.", exception.getMessage());
    }

    @Test
    void getByBookerIdAndStateTest_StateAll() {
        User user = createUser(1);
        User itemOwner = createUser(2);
        Item item = createItem(1, itemOwner.getId());
        Booking booking = createBooking(1, item, user);
        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(bookingRepository.findByBookerIdOrderByStartDesc(Mockito.anyLong(), any()))
               .thenReturn(List.of(booking));
        List<BookingDtoWithStatus> allBookings = bookingService.getByBookerIdAndState("ALL",
                user.getId(), 0, 10);
        assertNotNull(allBookings);
        assertEquals(1, allBookings.size());
    }

    @ParameterizedTest
    @CsvSource(value = {
            "ALL, 0, 2",
            "CURRENT, -1, 1",
            "PAST, -2, -1",
            "FUTURE, 1, 2",
            "WAITING, 0, 1",
            "REJECTED, 0, 1",
    })
    void getByBookerIdAndStateTest(String state, int addToStart, int addToEnd) {
        LocalDateTime start = LocalDateTime.now().plusDays(addToStart);
        LocalDateTime end = LocalDateTime.now().plusDays(addToEnd);
        User user1 = createUser(1);
        User user2 = createUser(2);
        Item item = createItem(1, user2.getId());
        Booking booking = createBooking(1, item, user1);
        booking.setBooker(user1);
        booking.setStart(start);
        booking.setEnd(end);

        Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(item));
        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        Mockito.when(bookingRepository.findByBookerIdOrderByStartDesc(Mockito.anyLong(), any()))
               .thenReturn(List.of(booking));
        Mockito.when(bookingRepository.findByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(
                       Mockito.anyLong(),any(), any(), any())).thenReturn(List.of(booking));
        Mockito.when(bookingRepository.findByBookerIdAndEndIsBeforeOrderByStartDesc(Mockito.anyLong(),
                               any(), any())).thenReturn(List.of(booking));
        Mockito.when(bookingRepository.findByBookerIdAndStartIsAfterOrderByStartDesc(Mockito.anyLong(),
                               any(), any())).thenReturn(List.of(booking));
        Mockito.when(bookingRepository.findByBookerIdAndStatusOrderByStartDesc(Mockito.anyLong(),
                       any(), any())).thenReturn(List.of(booking));
        List<BookingDtoWithStatus> bookings = bookingService.getByBookerIdAndState(state, user1.getId(), 0, 10);
        assertFalse(bookings.isEmpty());
        assertEquals(bookings.get(0).getId(), 1L);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "ALL, 0, 2",
            "CURRENT, -1, 1",
            "PAST, -2, -1",
            "FUTURE, 1, 2",
            "WAITING, 0, 1",
            "REJECTED, 0, 1",
    })
    void getByItemOwnerIdAndStateTest(String state, int addToStart, int addToEnd) {
        LocalDateTime start = LocalDateTime.now().plusDays(addToStart);
        LocalDateTime end = LocalDateTime.now().plusDays(addToEnd);
        User booker = createUser(1);
        User itemOwner = createUser(2);
        Item item = createItem(1, itemOwner.getId());
        Booking booking = createBooking(1, item, booker);
        booking.setBooker(booker);
        booking.setStart(start);
        booking.setEnd(end);
        Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(item));
        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        Mockito.when(bookingRepository.findByItemOwnerIdOrderByStartDesc(Mockito.anyLong(), any()))
               .thenReturn(List.of(booking));
        Mockito.when(bookingRepository.findByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(
                               Mockito.anyLong(),any(), any(), any())).thenReturn(List.of(booking));
        Mockito.when(bookingRepository.findByItemOwnerIdAndEndIsBeforeOrderByStartDesc(Mockito.anyLong(),
                               any(), any())).thenReturn(List.of(booking));
        Mockito.when(bookingRepository.findByItemOwnerIdAndStartIsAfterOrderByStartDesc(
                       Mockito.anyLong(),any(), any())).thenReturn(List.of(booking));
        Mockito.when(bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(Mockito.anyLong(),
                       any(), any())).thenReturn(List.of(booking));
        List<BookingDtoWithStatus> bookings = bookingService.getByItemOwnerIdAndState(state, itemOwner.getId(), 0, 10);
        assertFalse(bookings.isEmpty());
        assertEquals(bookings.get(0).getId(), 1L);
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
}