package ru.practicum.shareit.request;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
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

    private User user;
    private Item item;
    private ItemRequestDto itemRequestDto;
    private ItemRequest itemRequest;

    @BeforeEach
    void setUp() {
        user = createUser(1);
        itemRequestDto = createItemRequest(new ItemRequestDto(), 1, user);
        itemRequest = createItemRequest(new ItemRequest(), 1, user);
        item = createItem(1, user);
    }

    @Test
    void createTest_userNotFound() {
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemRequestService.create(itemRequestDto, user.getId(), LocalDateTime.now()));
        assertThat("User with id = " + user.getId() + " not exist.").isEqualTo(
                exception.getMessage());
    }

    @Test
    void createTest_descriptionBlank() {
        itemRequestDto.setDescription("");
        Mockito.when(userRepository.findById(Mockito.anyLong()))
               .thenReturn(Optional.of(user));
        ValidationException exception = assertThrows(ValidationException.class,
                () -> itemRequestService.create(itemRequestDto, user.getId(), LocalDateTime.now()));
        assertThat("Description can't be empty.").isEqualTo(exception.getMessage());
    }

    @Test
    void createTest_Ok() {
        Mockito.when(userRepository.findById(Mockito.anyLong()))
               .thenReturn(Optional.of(user));
        Mockito.when(itemRequestRepository.save(Mockito.any()))
               .thenReturn(itemRequest);
        ItemRequestDto itemFromService = itemRequestService.create(itemRequestDto, user.getId(),
                LocalDateTime.now());
        assertThat(itemRequestDto.getId()).isEqualTo(itemFromService.getId());
        assertThat(itemRequestDto.getRequester()).isEqualTo(itemFromService.getRequester());
    }

    @Test
    void getByIdTest_userNotFound() {
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemRequestService.getById(1L, 1L));
        assertThat("User not found.").isEqualTo(exception.getMessage());
    }

    @Test
    void getByIdTest_itemRequestNotFound() {
        Mockito.when(userRepository.existsById(user.getId()))
               .thenReturn(true);
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemRequestService.getById(1L, user.getId()));
        assertThat("Item request with id = 1 not exist.").isEqualTo(exception.getMessage());
    }

    @Test
    void getByIdTest_Ok() {
        Mockito.when(userRepository.existsById(user.getId()))
               .thenReturn(true);
        Mockito.when(itemRequestRepository.findById(itemRequest.getId()))
               .thenReturn(Optional.of(itemRequest));
        Mockito.when(itemRepository.findByRequestId(Mockito.anyLong()))
               .thenReturn(List.of(item));
        ItemRequestDto itemRequestFromService = itemRequestService.getById(itemRequest.getId(),
                user.getId());
        assertThat(itemRequest.getId()).isEqualTo(itemRequestFromService.getId());
    }

    @Test
    void getAllByUserTest_UserNotFound() {
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemRequestService.getAllByUser(1L));
        assertThat("User not found.").isEqualTo(exception.getMessage());
    }

    @Test
    void getAllByUserTest_Ok() {
        Mockito.when(userRepository.existsById(user.getId()))
               .thenReturn(true);
        Mockito.when(itemRequestRepository.findByRequesterId(Mockito.anyLong(), Mockito.any()))
               .thenReturn(List.of(itemRequest));
        List<ItemRequestDto> itemRequestsFromService = itemRequestService.getAllByUser(
                user.getId());
        assertThat(1).isEqualTo(itemRequestsFromService.size());
    }

    @Test
    void getAllTest_UserNotFound() {
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemRequestService.getAll(1L, 0, 10));
        assertThat("User not found.").isEqualTo(exception.getMessage());
    }

    @Test
    void getAllTest_validatePageFrom() {
        Mockito.when(userRepository.existsById(user.getId()))
               .thenReturn(true);
        ValidationException exception = assertThrows(ValidationException.class,
                () -> itemRequestService.getAll(1L, -1, 10));
        assertThat("It is not possible to start the display with a negative element.").isEqualTo(
                exception.getMessage());
    }

    @Test
    void getAllTest_validatePageSize() {
        Mockito.when(userRepository.existsById(user.getId()))
               .thenReturn(true);
        ValidationException exception = assertThrows(ValidationException.class,
                () -> itemRequestService.getAll(1L, 0, -10));
        assertThat("The number of records cannot be less than 1.").isEqualTo(
                exception.getMessage());
    }

    @Test
    void getAllTest_Ok() {
        Mockito.when(userRepository.existsById(user.getId()))
               .thenReturn(true);
        Mockito.when(itemRequestRepository.findAllByRequesterIdNot(Mockito.anyLong(), Mockito.any()))
               .thenReturn(List.of(itemRequest));
        List<ItemRequestDto> itemRequestsFromService = itemRequestService.getAll(user.getId(), 1,
                10);
        assertThat(1).isEqualTo(itemRequestsFromService.size());
    }

    private <T extends ItemRequest> T createItemRequest(T ir, long id, User user) {
        ir.setId(id);
        ir.setRequester(user);
        ir.setDescription("Description for item request " + id);
        ir.setCreated(LocalDateTime.of(2023, 12, 1, 8, 0));
        return ir;
    }

    private User createUser(long id) {
        User user = new User();
        user.setId(id);
        user.setName("User " + id);
        user.setEmail("user_" + id + "@email.ru");
        return user;
    }

    private Item createItem(long id, User user) {
        Item item = new Item();
        item.setId(id);
        item.setName("Name for item " + id);
        item.setDescription("Description dor item " + id);
        item.setOwnerId(user.getId());
        item.setAvailable(true);
        return item;
    }
}