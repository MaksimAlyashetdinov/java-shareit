package ru.practicum.shareit.booking;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
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
import ru.practicum.shareit.booking.dto.BookingDto;
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
    private User user;
    private User itemOwner;
    private Item item;
    private Booking booking;
    private BookingDto bookingDto;

    @BeforeEach
    void setUp() {
        user = createUser(1);
        itemOwner = createUser(2);
        item = createItem(1, 2);
        booking = createBooking(1, item, user);
        bookingDto = createBookingDto(2, item.getId(), user.getId());
    }

    @Test
    void createBookingTest_UserNotFound() {
        Mockito.when(userRepository.findById(1L))
               .thenReturn(Optional.of(new User()));
        Mockito.when(itemRepository.findById(Mockito.anyLong()))
               .thenReturn(Optional.of(item));
        Mockito.when(bookingRepository.save(any(Booking.class)))
               .thenReturn(new Booking());
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.create(2L,
                        BookingMapper.mapToBookingDto(booking)));
        assertThat("User with id = 2 not exist.").isEqualTo(exception.getMessage());
    }

    @Test
    void createBookingTest_ItemIdIsNull() {
        bookingDto.setItemId(null);
        Mockito.when(userRepository.findById(1L))
               .thenReturn(Optional.of(new User()));
        Mockito.when(itemRepository.findById(0L))
               .thenReturn(Optional.of(item));
        Mockito.when(bookingRepository.save(any(Booking.class)))
               .thenReturn(new Booking());
        ValidationException exception = assertThrows(ValidationException.class,
                () -> bookingService.create(user.getId(), bookingDto));
        assertThat("It is necessary to fill in all fields.").isEqualTo(exception.getMessage());
    }

    @Test
    void createBookingTest_ItemNotFound() {
        bookingDto.setItemId(2L);
        Mockito.when(userRepository.findById(1L))
               .thenReturn(Optional.of(new User()));
        Mockito.when(itemRepository.findById(1L))
               .thenReturn(Optional.of(item));
        Mockito.when(bookingRepository.save(any(Booking.class)))
               .thenReturn(new Booking());
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.create(user.getId(), bookingDto));
        assertThat("Item with id = 2 not exist.").isEqualTo(exception.getMessage());
    }

    @Test
    void createBookingTest_NotAvailable() {
        item.setAvailable(false);
        Mockito.when(userRepository.findById(1L))
               .thenReturn(Optional.of(new User()));
        Mockito.when(itemRepository.findById(Mockito.anyLong()))
               .thenReturn(Optional.of(item));
        Mockito.when(bookingRepository.save(any(Booking.class)))
               .thenReturn(new Booking());
        ValidationException exception = assertThrows(ValidationException.class,
                () -> bookingService.create(user.getId(), bookingDto));
        assertThat("Item is not available now.").isEqualTo(exception.getMessage());
    }

    @Test
    void createBookingTest_BookingByOwner() {
        item.setOwnerId(user.getId());
        Mockito.when(userRepository.findById(1L))
               .thenReturn(Optional.of(new User()));
        Mockito.when(itemRepository.findById(1L))
               .thenReturn(Optional.of(item));
        Mockito.when(bookingRepository.save(any(Booking.class)))
               .thenReturn(new Booking());
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.create(user.getId(), bookingDto));
        assertThat("You can't booking own items.").isEqualTo(exception.getMessage());
    }

    @Test
    void createBookingTest_StartBeforeCurrentDate() {
        bookingDto.setStart(LocalDateTime.of(1990, 1,1,1,0));
        Mockito.when(userRepository.findById(1L))
               .thenReturn(Optional.of(new User()));
        Mockito.when(itemRepository.findById(1L))
               .thenReturn(Optional.of(item));
        Mockito.when(bookingRepository.save(any(Booking.class)))
               .thenReturn(new Booking());
        ValidationException exception = assertThrows(ValidationException.class,
                () -> bookingService.create(user.getId(), bookingDto));
        assertThat("The start of the booking cannot be earlier than the current date or empty.").isEqualTo(exception.getMessage());
    }

    @Test
    void createBookingTest_EndBeforeStartDate() {
        bookingDto.setEnd(booking.getStart().minusDays(1));
        Mockito.when(userRepository.findById(1L))
               .thenReturn(Optional.of(new User()));
        Mockito.when(itemRepository.findById(1L))
               .thenReturn(Optional.of(item));
        Mockito.when(bookingRepository.save(any(Booking.class)))
               .thenReturn(new Booking());
        ValidationException exception = assertThrows(ValidationException.class,
                () -> bookingService.create(user.getId(), bookingDto));
        assertThat("The end of the booking cannot be earlier than the beginning or match it.").isEqualTo(exception.getMessage());
    }

    @Test
    void createBookingTest_Ok() {
        Mockito.when(userRepository.findById(1L))
               .thenReturn(Optional.of(new User()));
        Mockito.when(itemRepository.findById(1L))
               .thenReturn(Optional.of(item));
        Mockito.when(bookingRepository.save(Mockito.any(Booking.class)))
               .thenReturn(new Booking());
        Booking bookingFromService = bookingService.create(user.getId(), bookingDto);
        assertNotNull(bookingFromService);
    }

    @Test
    void getBookingByIdTest_BookingNotFound() {
        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(itemRepository.findById(1L))
               .thenReturn(Optional.of(item));
        Mockito.when(bookingRepository.save(booking)).thenReturn(new Booking());
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.getById(2L, user.getId()));
        assertThat("Booking not found.").isEqualTo(exception.getMessage());
    }

    @Test
    void getBookingByIdTest_NotBookerAndNotItemOwner() {
        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        Mockito.when(bookingRepository.save(booking)).thenReturn(booking);
        Mockito.when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.getById(1L, 3));
        assertThat("The user does not have access to the requested booking.").isEqualTo(exception.getMessage());
    }

    @Test
    void getBookingByIdTest_GetByBooker() {
        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        Mockito.when(bookingRepository.save(booking)).thenReturn(booking);
        Mockito.when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        Booking bookingFromService = bookingService.getById(1L, user.getId());
        assertNotNull(bookingFromService);
    }

    @Test
    void getBookingByIdTest_GetByItemOwner() {
        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        Mockito.when(bookingRepository.save(booking)).thenReturn(booking);
        Mockito.when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        Booking bookingFromService = bookingService.getById(1L, itemOwner.getId());
        assertNotNull(bookingFromService);
    }

    @Test
    void approveBookingTest_BookingNotFound() {
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.approveBooking(1L, true, itemOwner.getId()));
        assertThat("Booking not found.").isEqualTo(exception.getMessage());
    }

    @Test
    void approveBookingTest_NotOwner() {
        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        Mockito.when(bookingRepository.save(booking)).thenReturn(booking);
        Mockito.when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.approveBooking(1L, true, user.getId()));
        assertThat("This user can't change status.").isEqualTo(exception.getMessage());
    }

    @Test
    void approveBookingTest_AlreadyApprove() {
        booking.setStatus(BookingState.APPROVED);
        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        Mockito.when(bookingRepository.save(booking)).thenReturn(booking);
        Mockito.when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        ValidationException exception = assertThrows(ValidationException.class,
                () -> bookingService.approveBooking(1L, true, itemOwner.getId()));
        assertThat("This booking is approved before that.").isEqualTo(exception.getMessage());
    }

    @Test
    void approveBookingTest_ApproveTrue() {
        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        Mockito.when(bookingRepository.save(booking)).thenReturn(booking);
        Mockito.when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        Booking bookingFromService = bookingService.approveBooking(1L, true, itemOwner.getId());
        assertNotNull(bookingFromService);
        assertThat(BookingState.APPROVED).isEqualTo(bookingFromService.getStatus());
    }

    @Test
    void approveBookingTest_ApproveFalse() {
        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        Mockito.when(bookingRepository.save(booking)).thenReturn(booking);
        Mockito.when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        Booking bookingFromService = bookingService.approveBooking(1L, false, itemOwner.getId());
        assertNotNull(bookingFromService);
        assertThat(BookingState.REJECTED).isEqualTo(bookingFromService.getStatus());
    }

    @Test
    void getByBookerIdAndStateTest_PageFromLessThanZero() {
        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
        ValidationException exception = assertThrows(ValidationException.class,
                () -> bookingService.getByBookerIdAndState("ALL", 1, -1,10));
        assertThat("It is not possible to start the display with a negative element.").isEqualTo(exception.getMessage());
    }

    @Test
    void getByBookerIdAndStateTest_PageSizeLessThanOne() {
        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
        ValidationException exception = assertThrows(ValidationException.class,
                () -> bookingService.getByBookerIdAndState("ALL", 1, 0,0));
        assertThat("The number of records cannot be less than 1.").isEqualTo(exception.getMessage());
    }

    @Test
    void getByBookerIdAndStateTest_StateAll() {
        Mockito.when(userRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.when(bookingRepository.findByBookerIdOrderByStartDesc(Mockito.anyLong(), any()))
               .thenReturn(List.of(booking));
        List<BookingDtoWithStatus> allBookings = bookingService.getByBookerIdAndState("ALL",
                user.getId(), 0, 10);
        assertNotNull(allBookings);
        assertThat(1).isEqualTo(allBookings.size());
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
        List<BookingDtoWithStatus> bookings = bookingService.getByBookerIdAndState(state, user.getId(), 0, 10);
        assertFalse(bookings.isEmpty());
        assertThat(bookings.get(0).getId()).isEqualTo(1L);
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
        assertThat(bookings.get(0).getId()).isEqualTo(1L);
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
        booking.setEnd(LocalDateTime.of(2023,12,10,8,0));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingState.WAITING);
        return booking;
    }

    private BookingDto createBookingDto(long id, long itemId, long bookerId) {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(id);
        bookingDto.setItemId(itemId);
        bookingDto.setBookerId(bookerId);
        bookingDto.setStart(LocalDateTime.of(2023,12,1,8,0));
        bookingDto.setEnd(LocalDateTime.of(2023,12,10,8,0));
        return bookingDto;
    }
}