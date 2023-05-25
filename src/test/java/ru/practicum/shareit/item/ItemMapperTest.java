package ru.practicum.shareit.item;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.utils.ItemMapper;
import ru.practicum.shareit.user.User;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
public class ItemMapperTest {

    @Test
    void mapToItemDtoWithBookingsTest() {
        User itemOwner = createUser(1);
        User booker = createUser(2);
        Item item = createItem(1, itemOwner.getId());
        BookingDto lastBooking = createBookingDto(1, item, booker);
        BookingDto nextBooking = createBookingDto(2, item, booker);
        CommentDto commentDto = createCommentDto(1, booker);
        ItemDto itemDto = ItemMapper.mapToItemDtoWithBookings(item, lastBooking, nextBooking,
                List.of(commentDto));
        assertEquals(itemDto.getId(), item.getId());
        assertEquals(itemDto.getLastBooking(), lastBooking);
        assertEquals(itemDto.getNextBooking(), nextBooking);
        assertEquals(itemDto.getComments().size(), 1);
    }

    @Test
    void mapToItemDtoTest() {
        User itemOwner = createUser(1);
        User booker = createUser(2);
        Item item = createItem(1, itemOwner.getId());
        CommentDto commentDto = createCommentDto(1, booker);
        ItemDto itemDto = ItemMapper.mapToItemDto(item, List.of(commentDto));
        assertEquals(itemDto.getId(), item.getId());
        assertEquals(itemDto.getComments().size(), 1);
    }

    @Test
    void mapToItemTest() {
        User itemOwner = createUser(1);
        User booker = createUser(2);
        ItemDto itemDto = createItemDto(1, itemOwner.getId());
        Item item = ItemMapper.mapToItem(itemDto);
        assertEquals(itemDto.getId(), item.getId());
        assertEquals(itemDto.getName(), item.getName());
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