package ru.practicum.shareit.booking;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoWithStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDtoShort;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.utils.UserMapper;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {

    @Autowired
    ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    BookingService bookingService;

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private User itemOwner;
    private User booker;
    private Item item;

    @BeforeEach
    void setUp() {
        itemOwner = createUser(1L);
        booker = createUser(2L);
        item = createItem(1, itemOwner.getId());
    }

    @Test
    void createBookingTest_Ok() throws Exception {
        BookingDto inputBookingDto = createBookingDto(1, item, booker);
        inputBookingDto.setId(null);
        inputBookingDto.setBookerId(null);
        Booking outputBooking = createBooking(1, item, booker);
        Mockito
                .when(bookingService.create(anyLong(), any()))
                .thenReturn(outputBooking);
        mvc.perform(post("/bookings")
                   .header(USER_ID_HEADER, 2L)
                   .content(mapper.writeValueAsString(inputBookingDto))
                   .characterEncoding(StandardCharsets.UTF_8)
                   .contentType(MediaType.APPLICATION_JSON)
                   .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isOk())
           .andExpect(content().json(mapper.writeValueAsString(outputBooking)));
    }

    @Test
    void createBookingTest_NotFoundException() throws Exception {
        BookingDto inputBookingDto = createBookingDto(1, item, booker);
        inputBookingDto.setId(null);
        inputBookingDto.setBookerId(null);
        Mockito
                .when(bookingService.create(anyLong(), any()))
                .thenThrow(NotFoundException.class);
        mvc.perform(post("/bookings")
                   .header(USER_ID_HEADER, 2L)
                   .content(mapper.writeValueAsString(inputBookingDto))
                   .characterEncoding(StandardCharsets.UTF_8)
                   .contentType(MediaType.APPLICATION_JSON)
                   .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isNotFound());
    }

    @Test
    void createBookingTest_ValidationException() throws Exception {
        BookingDto inputBookingDto = createBookingDto(1, item, booker);
        inputBookingDto.setId(null);
        inputBookingDto.setBookerId(null);
        Mockito
                .when(bookingService.create(anyLong(), any()))
                .thenThrow(ValidationException.class);
        mvc.perform(post("/bookings")
                   .header(USER_ID_HEADER, 2L)
                   .content(mapper.writeValueAsString(inputBookingDto))
                   .characterEncoding(StandardCharsets.UTF_8)
                   .contentType(MediaType.APPLICATION_JSON)
                   .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isBadRequest());
    }

    @Test
    void changeStateOfBookingTest_Approve() throws Exception {
        Booking outputBooking = createBooking(1, item, booker);
        Mockito
                .when(bookingService.approveBooking(1, true, itemOwner.getId()))
                .thenReturn(outputBooking);
        mvc.perform(patch("/bookings" + "/1?approved=true")
                   .characterEncoding(StandardCharsets.UTF_8)
                   .contentType(MediaType.APPLICATION_JSON)
                   .header(USER_ID_HEADER, itemOwner.getId())
                   .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isOk())
           .andExpect(content().json(mapper.writeValueAsString(outputBooking)));
    }

    @Test
    void changeStateOfBookingTest_Reject() throws Exception {
        Booking outputBooking = createBooking(1, item, booker);
        Mockito
                .when(bookingService.approveBooking(1, false, itemOwner.getId()))
                .thenReturn(outputBooking);
        mvc.perform(patch("/bookings" + "/1?approved=false")
                   .characterEncoding(StandardCharsets.UTF_8)
                   .contentType(MediaType.APPLICATION_JSON)
                   .header(USER_ID_HEADER, itemOwner.getId())
                   .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isOk())
           .andExpect(content().json(mapper.writeValueAsString(outputBooking)));
    }

    @Test
    void changeStateOfBookingTest_NotFoundException() throws Exception {
        Mockito
                .when(bookingService.approveBooking(1, true, itemOwner.getId()))
                .thenThrow(NotFoundException.class);
        mvc.perform(patch("/bookings" + "/1?approved=true")
                   .characterEncoding(StandardCharsets.UTF_8)
                   .contentType(MediaType.APPLICATION_JSON)
                   .header(USER_ID_HEADER, itemOwner.getId())
                   .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isNotFound());
    }

    @Test
    void changeStateOfBookingTest_ValidationException() throws Exception {
        Mockito
                .when(bookingService.approveBooking(1, true, itemOwner.getId()))
                .thenThrow(ValidationException.class);
        mvc.perform(patch("/bookings" + "/1?approved=true")
                   .characterEncoding(StandardCharsets.UTF_8)
                   .contentType(MediaType.APPLICATION_JSON)
                   .header(USER_ID_HEADER, itemOwner.getId())
                   .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isBadRequest());
    }

    @Test
    void getByIdTest_Ok() throws Exception {
        Booking outputBooking = createBooking(1, item, booker);
        Mockito
                .when(bookingService.getById(anyLong(), anyLong()))
                .thenReturn(outputBooking);
        mvc.perform(get("/bookings" + "/1")
                   .characterEncoding(StandardCharsets.UTF_8)
                   .contentType(MediaType.APPLICATION_JSON)
                   .header(USER_ID_HEADER, 1L)
                   .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isOk())
           .andExpect(content().json(mapper.writeValueAsString(outputBooking)));
    }

    @Test
    void getByIdTest_NotFoundException() throws Exception {
        Mockito
                .when(bookingService.getById(anyLong(), anyLong()))
                .thenThrow(NotFoundException.class);
        mvc.perform(get("/bookings" + "/1")
                   .characterEncoding(StandardCharsets.UTF_8)
                   .contentType(MediaType.APPLICATION_JSON)
                   .header(USER_ID_HEADER, 1L)
                   .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isNotFound());
    }

    @Test
    void getByStateAndUserIdTest_Ok() throws Exception {
        BookingDtoWithStatus outputBooking = createBookingDtoWithStatus(1, item, booker);
        Mockito
                .when(bookingService.getByBookerIdAndState("ALL", booker.getId(), 0, 10))
                .thenReturn(List.of(outputBooking));
        mvc.perform(get("/bookings" + "?state=ALL")
                   .characterEncoding(StandardCharsets.UTF_8)
                   .contentType(MediaType.APPLICATION_JSON)
                   .header(USER_ID_HEADER, booker.getId())
                   .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isOk())
           .andExpect(content().json(mapper.writeValueAsString(List.of(outputBooking))));
    }

    @Test
    void getByStateAndUserIdTest_NotFoundException() throws Exception {
        Mockito
                .when(bookingService.getByBookerIdAndState("ALL", booker.getId(), 0, 10))
                .thenThrow(NotFoundException.class);
        mvc.perform(get("/bookings" + "?state=ALL")
                   .characterEncoding(StandardCharsets.UTF_8)
                   .contentType(MediaType.APPLICATION_JSON)
                   .header(USER_ID_HEADER, booker.getId())
                   .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isNotFound());
    }

    @Test
    void getByStateAndUserIdTest_ValidationException() throws Exception {
        Mockito
                .when(bookingService.getByBookerIdAndState("ALL", booker.getId(), 0, 10))
                .thenThrow(ValidationException.class);
        mvc.perform(get("/bookings" + "?state=ALL")
                   .characterEncoding(StandardCharsets.UTF_8)
                   .contentType(MediaType.APPLICATION_JSON)
                   .header(USER_ID_HEADER, booker.getId())
                   .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isBadRequest());
    }

    @Test
    void getByItemOwnerIdAndStateTest_Ok() throws Exception {
        BookingDtoWithStatus outputBooking = createBookingDtoWithStatus(1, item, booker);
        Mockito
                .when(bookingService.getByItemOwnerIdAndState("ALL", itemOwner.getId(), 0, 10))
                .thenReturn(List.of(outputBooking));
        mvc.perform(get("/bookings" + "/owner?state=ALL")
                   .characterEncoding(StandardCharsets.UTF_8)
                   .contentType(MediaType.APPLICATION_JSON)
                   .header(USER_ID_HEADER, itemOwner.getId())
                   .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isOk())
           .andExpect(content().json(mapper.writeValueAsString(List.of(outputBooking))));
    }

    @Test
    void getByItemOwnerIdAndStateTest_NotFoundException() throws Exception {
        Mockito
                .when(bookingService.getByItemOwnerIdAndState("ALL", itemOwner.getId(), 0, 10))
                .thenThrow(NotFoundException.class);
        mvc.perform(get("/bookings" + "/owner?state=ALL")
                   .characterEncoding(StandardCharsets.UTF_8)
                   .contentType(MediaType.APPLICATION_JSON)
                   .header(USER_ID_HEADER, itemOwner.getId())
                   .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isNotFound());
    }

    @Test
    void getByItemOwnerIdAndStateTest_ValidationException() throws Exception {
        Mockito
                .when(bookingService.getByItemOwnerIdAndState("ALL", itemOwner.getId(), 0, 10))
                .thenThrow(ValidationException.class);
        mvc.perform(get("/bookings" + "/owner?state=ALL")
                   .characterEncoding(StandardCharsets.UTF_8)
                   .contentType(MediaType.APPLICATION_JSON)
                   .header(USER_ID_HEADER, itemOwner.getId())
                   .accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isBadRequest());
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
        booking.setStart(LocalDateTime.of(2023, 12, 1, 8, 0));
        booking.setEnd(LocalDateTime.of(2023, 12, 10, 8, 0));
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

    private BookingDtoWithStatus createBookingDtoWithStatus(long id, Item item, User booker) {
        BookingDtoWithStatus bookingDtoWithStatus = new BookingDtoWithStatus();
        bookingDtoWithStatus.setId(id);
        bookingDtoWithStatus.setItem(new ItemDtoShort(item.getId(), item.getName()));
        bookingDtoWithStatus.setBooker(UserMapper.mapToUserDto(booker));
        bookingDtoWithStatus.setStatus(BookingState.WAITING);
        return bookingDtoWithStatus;
    }
}