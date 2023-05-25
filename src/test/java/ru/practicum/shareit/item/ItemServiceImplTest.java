package ru.practicum.shareit.item;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoRequest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.item.utils.CommentMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.storage.UserRepository;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
public class ItemServiceImplTest {

    @InjectMocks
    ItemServiceImpl itemService;

    @Mock
    ItemRepository itemRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    CommentRepository commentRepository;

    @Mock
    BookingRepository bookingRepository;

    @Test
    void createItemTest_ItemNameEmpty() {
        User itemOwner = createUser(1);
        Item item = createItem(1, itemOwner.getId());
        item.setName(null);
        ValidationException exception = assertThrows(ValidationException.class,
                () -> itemService.createItem(1L, item));
        assertEquals("You must specify the name.", exception.getMessage());
    }

    @Test
    void createItemTest_ItemDescriptionEmpty() {
        User itemOwner = createUser(1);
        Item item = createItem(1, itemOwner.getId());
        item.setDescription(null);
        ValidationException exception = assertThrows(ValidationException.class,
                () -> itemService.createItem(1L, item));
        assertEquals("You must specify the description.", exception.getMessage());
    }

    @Test
    void createItemTest_ItemAvailableNull() {
        User itemOwner = createUser(1);
        Item item = createItem(1, itemOwner.getId());
        item.setAvailable(null);
        ValidationException exception = assertThrows(ValidationException.class,
                () -> itemService.createItem(1L, item));
        assertEquals("You must specify the available.", exception.getMessage());
    }

    @Test
    void createItemTest_UserNotFound() {
        User itemOwner = createUser(1);
        Item item = createItem(1, itemOwner.getId());
        Mockito.when(userRepository.existsById(anyLong())).thenReturn(false);
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemService.createItem(1L, item));
        assertEquals("User with id = 1 not exist.", exception.getMessage());
    }

    @Test
    void createItemTest_Ok() {
        User itemOwner = createUser(1);
        Item item = createItem(1, itemOwner.getId());
        Mockito.when(userRepository.existsById(anyLong())).thenReturn(true);
        Mockito.when(itemRepository.save(item)).thenReturn(item);
        Item itemFromService = itemService.createItem(1, item);
        assertEquals(item.getId(), itemFromService.getId());
        assertEquals(item.getName(), itemFromService.getName());
    }

    @Test
    void getByIdTest_ItemNotFound() {
        User itemOwner = createUser(1);
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemService.getById(itemOwner.getId(), 0));
        assertEquals("Item not found.", exception.getMessage());
    }

    @Test
    void getByIdTest_ItemOwner() {
        User itemOwner = createUser(1);
        User user = createUser(2);
        Item item = createItem(1, itemOwner.getId());
        Comment comment = createComment(1, user, item);
        Booking lastBooking = createBooking(1, item, user);
        Booking nextBooking = createBooking(2, item, user);
        Mockito.when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        Mockito.when(commentRepository.findAllByItemId(item.getId())).thenReturn(List.of(comment));
        Mockito.when(bookingRepository.findFirstByItemIdAndStartIsBeforeAndStatusIsOrderByStartDesc(
                       Mockito.anyLong(), Mockito.any(LocalDateTime.class),
                       Mockito.any(BookingState.class))).thenReturn(lastBooking);
        Mockito.when(bookingRepository.findFirstByItemIdAndStartIsAfterAndStatusIsOrderByStartAsc(
                       Mockito.anyLong(), Mockito.any(LocalDateTime.class),
                       Mockito.any(BookingState.class))).thenReturn(nextBooking);
        ItemDto itemFromService = itemService.getById(itemOwner.getId(), item.getId());
        assertEquals(item.getId(), itemFromService.getId());
        assertEquals(item.getName(), itemFromService.getName());
        assertEquals(lastBooking.getId(), itemFromService.getLastBooking().getId());
        assertEquals(nextBooking.getId(), itemFromService.getNextBooking().getId());
        assertEquals(1, itemFromService.getComments().size());
    }

    @Test
    void getByIdTest_NotItemOwner() {
        User itemOwner = createUser(1);
        User user = createUser(2);
        Item item = createItem(1, itemOwner.getId());
        Comment comment = createComment(1, user, item);
        Booking lastBooking = createBooking(1, item, user);
        Booking nextBooking = createBooking(2, item, user);
        Mockito.when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        Mockito.when(commentRepository.findAllByItemId(item.getId())).thenReturn(List.of(comment));
        Mockito.when(bookingRepository.findFirstByItemIdAndStartIsBeforeAndStatusIsOrderByStartDesc(
                       Mockito.anyLong(), Mockito.any(LocalDateTime.class),
                       Mockito.any(BookingState.class))).thenReturn(lastBooking);
        Mockito.when(bookingRepository.findFirstByItemIdAndStartIsAfterAndStatusIsOrderByStartAsc(
                       Mockito.anyLong(), Mockito.any(LocalDateTime.class),
                       Mockito.any(BookingState.class))).thenReturn(nextBooking);
        ItemDto itemFromService = itemService.getById(user.getId(), item.getId());
        assertEquals(item.getId(), itemFromService.getId());
        assertEquals(item.getName(), itemFromService.getName());
        assertEquals(null, itemFromService.getLastBooking());
        assertEquals(null, itemFromService.getNextBooking());
        assertEquals(1, itemFromService.getComments().size());
    }

    @Test
    void getByNameTest_NameIsBlank() {
        User itemOwner = createUser(1);
        List<Item> itemsFromService = itemService.getByName("", 0, 10);
        assertEquals(0, itemsFromService.size());
    }

    @Test
    void getByNameTest_validatePageFrom() {
        User itemOwner = createUser(1);
        Item item = createItem(1, itemOwner.getId());
        ValidationException exception = assertThrows(ValidationException.class,
                () -> itemService.getByName(item.getName(), -1, 10));
        assertEquals("It is not possible to start the display with a negative element.",
                exception.getMessage());
    }

    @Test
    void getByNameTest_validatePageSize() {
        User itemOwner = createUser(1);
        Item item = createItem(1, itemOwner.getId());
        ValidationException exception = assertThrows(ValidationException.class,
                () -> itemService.getByName(item.getName(), 0, -10));
        assertEquals("The number of records cannot be less than 1.", exception.getMessage());
    }

    @Test
    void getByNameTest_Ok() {
        User itemOwner = createUser(1);
        Item item = createItem(1, itemOwner.getId());
        Mockito.when(itemRepository.findAllByName(Mockito.anyString(), Mockito.any()))
               .thenReturn(List.of(item));
        List<Item> itemsFromService = itemService.getByName(item.getName(), 0, 10);
        assertEquals(1, itemsFromService.size());
    }

    @Test
    void getAllItemsByUserIdTest() {
        User itemOwner = createUser(1);
        Item item = createItem(1, itemOwner.getId());
        Mockito.when(userRepository.existsById(anyLong())).thenReturn(true);
        Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(item));
        Mockito.when(commentRepository.findAllByItemId(Mockito.anyLong())).thenReturn(new ArrayList<>());
        Mockito.when(itemRepository.findAllByOwnerId(Mockito.anyLong(), Mockito.any()))
               .thenReturn(List.of(item));
        List<ItemDto> itemsFromService = itemService.getAllItemsByUserId(itemOwner.getId(), 0, 10);
        assertEquals(1, itemsFromService.size());
    }

    @Test
    void updateItemTest_NotItemOwner() {
        User itemOwner = createUser(1);
        Item item = createItem(1, itemOwner.getId());
        Mockito.when(userRepository.existsById(anyLong())).thenReturn(true);
        Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(item));
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemService.updateItem(2, 1, item));
        assertEquals("This item can update only user with id = " + itemOwner.getId(),
                exception.getMessage());
    }

    @Test
    void updateItemTest_Ok() {
        User itemOwner = createUser(1);
        Item item = createItem(1, itemOwner.getId());
        Item updateItem = item;
        updateItem.setName("Update name");
        updateItem.setDescription("Update description");
        updateItem.setAvailable(true);
        Mockito.when(userRepository.existsById(anyLong())).thenReturn(true);
        Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(item));
        Mockito.when(itemRepository.save(Mockito.any())).thenReturn(updateItem);
        Item itemFrom = itemService.updateItem(itemOwner.getId(), item.getId(), item);
        assertEquals(updateItem.getName(), itemFrom.getName());
        assertEquals(updateItem.getDescription(), itemFrom.getDescription());
        assertEquals(updateItem.getAvailable(), itemFrom.getAvailable());
    }

    @Test
    void deleteItem_ItemNotFound() {
        User itemOwner = createUser(1);
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemService.deleteItem(0));
        assertEquals("Item not found.", exception.getMessage());
    }

    @Test
    void deleteItem_Ok() {
        User itemOwner = createUser(1);
        Item item = createItem(1, itemOwner.getId());
        Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(item));
        itemService.deleteItem(item.getId());
    }

    @Test
    void addCommentToItem_UserNotFound() {
        User itemOwner = createUser(1);
        Item item = createItem(1, itemOwner.getId());
        CommentDtoRequest commentDtoRequest = createCommentDtoRequest(1);
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemService.addCommentToItem(2, item.getId(), commentDtoRequest));
        assertEquals("User not found.", exception.getMessage());
    }

    @Test
    void addCommentToItem_ItemNotFound() {
        User itemOwner = createUser(1);
        User user = createUser(2);
        Item item = createItem(1, itemOwner.getId());
        CommentDtoRequest commentDtoRequest = createCommentDtoRequest(1);
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemService.addCommentToItem(2, item.getId(), commentDtoRequest));
        assertEquals("Item not found.", exception.getMessage());
    }

    @Test
    void addCommentToItem_CommentTextBlank() {
        User itemOwner = createUser(1);
        User user = createUser(2);
        Item item = createItem(1, itemOwner.getId());
        CommentDtoRequest commentDtoRequest = createCommentDtoRequest(1);
        commentDtoRequest.setText("");
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));
        Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(item));
        ValidationException exception = assertThrows(ValidationException.class,
                () -> itemService.addCommentToItem(2, item.getId(), commentDtoRequest));
        assertEquals("Text of comment can't be empty.", exception.getMessage());
    }

    @Test
    void addCommentToItem_UserNotUseItem() {
        User itemOwner = createUser(1);
        User user = createUser(2);
        Item item = createItem(1, itemOwner.getId());
        CommentDtoRequest commentDtoRequest = createCommentDtoRequest(1);
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));
        Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(item));
        Mockito.when(bookingRepository.findFirstByItemIdAndBookerIdAndStatusAndEndIsBefore(
                       Mockito.anyLong(), Mockito.anyLong(),
                       Mockito.any(), Mockito.any())).thenReturn(Optional.empty());
        ValidationException exception = assertThrows(ValidationException.class,
                () -> itemService.addCommentToItem(2, item.getId(), commentDtoRequest));
        assertEquals("This user can't add comment to this item.", exception.getMessage());
    }

    @Test
    void addCommentToItem_Ok() {
        User itemOwner = createUser(1);
        User user = createUser(2);
        Item item = createItem(1, itemOwner.getId());
        CommentDtoRequest commentDtoRequest = createCommentDtoRequest(1);
        Comment comment = CommentMapper.toComment(user, item, commentDtoRequest,LocalDateTime.now());
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));
        Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(item));
        Mockito.when(bookingRepository.findFirstByItemIdAndBookerIdAndStatusAndEndIsBefore(
                       Mockito.anyLong(), Mockito.anyLong(),
                       Mockito.any(), Mockito.any())).thenReturn(Optional.of(new Booking()));
        Mockito.when(commentRepository.save(Mockito.any())).thenReturn(comment);
        CommentDto commentFromService = itemService.addCommentToItem(user.getId(), item.getId(),
                commentDtoRequest);
        assertEquals(commentDtoRequest.getId(), commentFromService.getId());
        assertEquals(commentDtoRequest.getText(), commentFromService.getText());
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

    private Comment createComment(long id, User user, Item item) {
        Comment comment = new Comment();
        comment.setId(id);
        comment.setAuthor(user);
        comment.setItem(item);
        comment.setText("Text for comment " + id);
        return comment;
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

    private CommentDtoRequest createCommentDtoRequest(long id) {
        CommentDtoRequest commentDtoRequest = new CommentDtoRequest();
        commentDtoRequest.setId(id);
        commentDtoRequest.setText("Text for comment " + id);
        return commentDtoRequest;
    }
}