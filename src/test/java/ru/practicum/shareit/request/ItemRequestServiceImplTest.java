package ru.practicum.shareit.request;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.storage.UserRepository;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceImplTest {

    @InjectMocks
    ItemRequestServiceImpl itemRequestService;

    @Mock
    ItemRepository itemRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    ItemRequestRepository itemRequestRepository;

    @Mock
    BookingRepository bookingRepository;

    @Test
    void createTest_userNotFound() {
        User user = createUser(1);
        ItemRequestDto itemRequestDto = createItemRequestDto(1, user);
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemRequestService.create(itemRequestDto, user.getId(), LocalDateTime.now()));
        assertEquals("User with id = " + user.getId() + " not exist.", exception.getMessage());
    }

    @Test
    void createTest_descriptionBlank() {
        User user = createUser(1);
        ItemRequestDto itemRequestDto = createItemRequestDto(1, user);
        itemRequestDto.setDescription("");
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));
        ValidationException exception = assertThrows(ValidationException.class,
                () -> itemRequestService.create(itemRequestDto, user.getId(), LocalDateTime.now()));
        assertEquals("Description can't be empty.", exception.getMessage());
    }

    @Test
    void createTest_Ok() {
        User user = createUser(1);
        ItemRequestDto itemRequestDto = createItemRequestDto(1, user);
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto);
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));
        Mockito.when(itemRequestRepository.save(Mockito.any())).thenReturn(itemRequest);
        ItemRequestDto itemFromService = itemRequestService.create(itemRequestDto, user.getId(),
                LocalDateTime.now());
        assertEquals(itemRequestDto.getId(), itemFromService.getId());
        assertEquals(itemRequestDto.getRequester(), itemFromService.getRequester());
    }

    @Test
    void getByIdTest_userNotFound() {
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemRequestService.getById(1L, 1L));
        assertEquals("User not found.", exception.getMessage());
    }

    @Test
    void getByIdTest_itemRequestNotFound() {
        User user = createUser(1);
        Mockito.when(userRepository.existsById(user.getId())).thenReturn(true);
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemRequestService.getById(1L, user.getId()));
        assertEquals("Item request with id = 1 not exist.", exception.getMessage());
    }

    @Test
    void getByIdTest_Ok() {
        User user = createUser(1);
        ItemRequest itemRequest = createItemRequest(1, user);
        Mockito.when(userRepository.existsById(user.getId())).thenReturn(true);
        Mockito.when(itemRequestRepository.findById(itemRequest.getId()))
               .thenReturn(Optional.of(itemRequest));
        Mockito.when(itemRepository.findByRequestId(Mockito.anyLong()))
               .thenReturn(List.of(new Item()));
        ItemRequestDto itemRequestFromService = itemRequestService.getById(itemRequest.getId(),
                user.getId());
        assertEquals(itemRequest.getId(), itemRequestFromService.getId());
    }

    @Test
    void getAllByUserTest_UserNotFound() {
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemRequestService.getAllByUser(1L));
        assertEquals("User not found.", exception.getMessage());
    }

    @Test
    void getAllByUserTest_Ok() {
        User user = createUser(1);
        Mockito.when(userRepository.existsById(user.getId())).thenReturn(true);
        ItemRequest itemRequest = createItemRequest(1, user);
        Mockito.when(itemRequestRepository.findByRequesterId(Mockito.anyLong(), Mockito.any()))
               .thenReturn(List.of(itemRequest));
        List<ItemRequestDto> itemRequestsFromService = itemRequestService.getAllByUser(user.getId());
        assertEquals(1, itemRequestsFromService.size());
    }

    @Test
    void getAllTest_UserNotFound() {
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemRequestService.getAll(1L, 0, 10));
        assertEquals("User not found.", exception.getMessage());
    }

    @Test
    void getAllTest_validatePageFrom() {
        User user = createUser(1);
        Mockito.when(userRepository.existsById(user.getId())).thenReturn(true);
        ValidationException exception = assertThrows(ValidationException.class,
                () -> itemRequestService.getAll(1L, -1, 10));
        assertEquals("It is not possible to start the display with a negative element.",
                exception.getMessage());
    }

    @Test
    void getAllTest_validatePageSize() {
        User user = createUser(1);
        Mockito.when(userRepository.existsById(user.getId())).thenReturn(true);
        ValidationException exception = assertThrows(ValidationException.class,
                () -> itemRequestService.getAll(1L, 0, -10));
        assertEquals("The number of records cannot be less than 1.", exception.getMessage());
    }

    @Test
    void getAllTest_Ok() {
        User user = createUser(1);
        ItemRequest itemRequest = createItemRequest(1, user);
        Mockito.when(userRepository.existsById(user.getId())).thenReturn(true);
        Mockito.when(itemRequestRepository.findAllByRequesterIdNot(Mockito.anyLong(),Mockito.any()))
               .thenReturn(List.of(itemRequest));
        List<ItemRequestDto> itemRequestsFromService = itemRequestService.getAll(user.getId(), 1,
                10);
        assertEquals(1, itemRequestsFromService.size());
    }

    private ItemRequest createItemRequest(long id, User user) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(id);
        itemRequest.setRequester(user);
        itemRequest.setDescription("Description for item request " + id);
        return itemRequest;
    }

    private ItemRequestDto createItemRequestDto(long id, User user) {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(id);
        itemRequestDto.setRequester(user);
        itemRequestDto.setDescription("Description for item request " + id);
        return itemRequestDto;
    }

    private User createUser(long id) {
        User user = new User();
        user.setId(id);
        user.setName("User " + id);
        user.setEmail("user_" + id + "@email.ru");
        return user;
    }
}