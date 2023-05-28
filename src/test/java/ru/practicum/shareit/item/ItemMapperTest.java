package ru.practicum.shareit.item;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.utils.ItemMapper;
import ru.practicum.shareit.user.User;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
public class ItemMapperTest {

    private User itemOwner;
    private User booker;
    private Item item;
    private ItemDto itemDto;
    private BookingDto lastBooking;
    private BookingDto nextBooking;
    private CommentDto commentDto;

    @BeforeEach
    void setUp() {
        itemOwner = createUser(1);
        booker = createUser(2);
        item = createItem(1, itemOwner.getId());
        lastBooking = createBookingDto(1, item, booker);
        nextBooking = createBookingDto(2, item, booker);
        commentDto = createCommentDto(1, booker);
        itemDto = createItemDto(1, itemOwner.getId());
    }

    @Test
    void mapToItemDtoWithBookingsTest_Ok() {
        ItemDto itemDto = ItemMapper.mapToItemDtoWithBookings(item, lastBooking, nextBooking,
                List.of(commentDto));
        assertThat(itemDto.getId()).isEqualTo(item.getId());
        assertThat(itemDto.getLastBooking()).isEqualTo(lastBooking);
        assertThat(itemDto.getNextBooking()).isEqualTo(nextBooking);
        assertThat(itemDto.getComments().size()).isEqualTo(1);
    }

    @Test
    void mapToItemDtoWithBookingsTest_idIsNull() {
        item.setId(null);
        ValidationException exception = assertThrows(ValidationException.class,
                () -> ItemMapper.mapToItemDtoWithBookings(item, lastBooking, nextBooking,
                        List.of(commentDto)));
        assertThat("All item fields must be filled in.").isEqualTo(exception.getMessage());
    }

    @Test
    void mapToItemDtoWithBookingsTest_nameIsNull() {
        item.setName(null);
        ValidationException exception = assertThrows(ValidationException.class,
                () -> ItemMapper.mapToItemDtoWithBookings(item, lastBooking, nextBooking,
                        List.of(commentDto)));
        assertThat("All item fields must be filled in.").isEqualTo(exception.getMessage());
    }

    @Test
    void mapToItemDtoWithBookingsTest_descriptionIsNull() {
        item.setDescription(null);
        ValidationException exception = assertThrows(ValidationException.class,
                () -> ItemMapper.mapToItemDtoWithBookings(item, lastBooking, nextBooking,
                        List.of(commentDto)));
        assertThat("All item fields must be filled in.").isEqualTo(exception.getMessage());
    }

    @Test
    void mapToItemDtoWithBookingsTest_availableIsNull() {
        item.setAvailable(null);
        ValidationException exception = assertThrows(ValidationException.class,
                () -> ItemMapper.mapToItemDtoWithBookings(item, lastBooking, nextBooking,
                        List.of(commentDto)));
        assertThat("All item fields must be filled in.").isEqualTo(exception.getMessage());
    }

    @Test
    void mapToItemDtoWithBookingsTest_ownerIsNull() {
        item.setOwnerId(null);
        ValidationException exception = assertThrows(ValidationException.class,
                () -> ItemMapper.mapToItemDtoWithBookings(item, lastBooking, nextBooking,
                        List.of(commentDto)));
        assertThat("All item fields must be filled in.").isEqualTo(exception.getMessage());
    }

    @Test
    void mapToItemDtoTest_Ok() {
        ItemDto itemDto = ItemMapper.mapToItemDto(item, List.of(commentDto));
        assertThat(itemDto.getId()).isEqualTo(item.getId());
        assertThat(itemDto.getComments().size()).isEqualTo(1);
    }

    @Test
    void mapToItemDtoTest_idIsNull() {
        item.setId(null);
        ValidationException exception = assertThrows(ValidationException.class,
                () -> ItemMapper.mapToItemDto(item, List.of(commentDto)));
        assertThat("All item fields must be filled in.").isEqualTo(exception.getMessage());
    }

    @Test
    void mapToItemDtoTest_nameIsNull() {
        item.setName(null);
        ValidationException exception = assertThrows(ValidationException.class,
                () -> ItemMapper.mapToItemDto(item, List.of(commentDto)));
        assertThat("All item fields must be filled in.").isEqualTo(exception.getMessage());
    }

    @Test
    void mapToItemDtoTest_descriptionIsNull() {
        item.setDescription(null);
        ValidationException exception = assertThrows(ValidationException.class,
                () -> ItemMapper.mapToItemDto(item, List.of(commentDto)));
        assertThat("All item fields must be filled in.").isEqualTo(exception.getMessage());
    }

    @Test
    void mapToItemDtoTest_availableIsNull() {
        item.setAvailable(null);
        ValidationException exception = assertThrows(ValidationException.class,
                () -> ItemMapper.mapToItemDto(item, List.of(commentDto)));
        assertThat("All item fields must be filled in.").isEqualTo(exception.getMessage());
    }

    @Test
    void mapToItemDtoTest_ownerIsNull() {
        item.setOwnerId(null);
        ValidationException exception = assertThrows(ValidationException.class,
                () -> ItemMapper.mapToItemDto(item, List.of(commentDto)));
        assertThat("All item fields must be filled in.").isEqualTo(exception.getMessage());
    }

    @Test
    void mapToItemTest_Ok() {
        Item item = ItemMapper.mapToItem(itemDto);
        assertThat(itemDto.getId()).isEqualTo(item.getId());
        assertThat(itemDto.getName()).isEqualTo(item.getName());
    }

    @Test
    void mapToItemTest_idIsNull() {
        itemDto.setId(null);
        ValidationException exception = assertThrows(ValidationException.class,
                () -> ItemMapper.mapToItem(itemDto));
        assertThat("All itemDto fields must be filled in.").isEqualTo(exception.getMessage());
    }

    @Test
    void mapToItemTest_nameIsNull() {
        itemDto.setName(null);
        ValidationException exception = assertThrows(ValidationException.class,
                () -> ItemMapper.mapToItem(itemDto));
        assertThat("All itemDto fields must be filled in.").isEqualTo(exception.getMessage());
    }

    @Test
    void mapToItemTest_descriptionIsNull() {
        itemDto.setDescription(null);
        ValidationException exception = assertThrows(ValidationException.class,
                () -> ItemMapper.mapToItem(itemDto));
        assertThat("All itemDto fields must be filled in.").isEqualTo(exception.getMessage());
    }

    @Test
    void mapToItemTest_availableIsNull() {
        itemDto.setAvailable(null);
        ValidationException exception = assertThrows(ValidationException.class,
                () -> ItemMapper.mapToItem(itemDto));
        assertThat("All itemDto fields must be filled in.").isEqualTo(exception.getMessage());
    }

    @Test
    void mapToItemTest_ownerIsNull() {
        itemDto.setOwnerId(null);
        ValidationException exception = assertThrows(ValidationException.class,
                () -> ItemMapper.mapToItem(itemDto));
        assertThat("All itemDto fields must be filled in.").isEqualTo(exception.getMessage());
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

    private ItemDto createItemDto(long id, long ownerId) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(id);
        itemDto.setName("Name for item " + id);
        itemDto.setDescription("Description for item " + id);
        itemDto.setOwnerId(ownerId);
        itemDto.setAvailable(true);
        return itemDto;
    }

    private BookingDto createBookingDto(long id, Item item, User booker) {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(id);
        bookingDto.setItemId(item.getId());
        bookingDto.setBookerId(booker.getId());
        return bookingDto;
    }

    private CommentDto createCommentDto(long id, User user) {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(id);
        commentDto.setText("Text for comment " + id);
        commentDto.setAuthorName(user.getName());
        return commentDto;
    }
}