package ru.practicum.shareit.request;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
public class ItemRequestMapperTest {

    @Test
    void toItemRequestDtoTest() {
        User user = createUser(1);
        ItemRequest itemRequest = createItemRequest(1, user);
        ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest);
        assertEquals(itemRequest.getId(), itemRequestDto.getId());
        assertEquals(itemRequest.getRequester(), itemRequestDto.getRequester());
    }

    @Test
    void toItemRequestTest() {
        User user = createUser(1);
        ItemRequestDto itemRequestDto = createItemRequestDto(1, user);
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto);
        assertEquals(itemRequestDto.getId(), itemRequest.getId());
        assertEquals(itemRequestDto.getRequester(), itemRequest.getRequester());
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