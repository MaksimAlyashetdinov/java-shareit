package ru.practicum.shareit.request;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
public class ItemRequestMapperTest {

    private User user;
    private ItemRequest itemRequest;
    private ItemRequestDto itemRequestDto;

    @BeforeEach
    void setUp() {
        user = createUser(1);
        itemRequest = createItemRequest(1, user);
        itemRequestDto = createItemRequestDto(1, user);
    }

    @Test
    void toItemRequestDtoTest_Ok() {
        ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest);
        assertThat(itemRequest.getId()).isEqualTo(itemRequestDto.getId());
        assertThat(itemRequest.getRequester()).isEqualTo(itemRequestDto.getRequester());
    }

    @Test
    void toItemRequestDtoTest_requesterIsNull() {
        itemRequest.setRequester(null);
        ValidationException exception = assertThrows(ValidationException.class,
                () -> ItemRequestMapper.toItemRequestDto(itemRequest));
        assertThat("All itemRequest fields must be filled in.").isEqualTo(exception.getMessage());
    }

    @Test
    void toItemRequestDtoTest_descriptionIsNull() {
        itemRequest.setDescription(null);
        ValidationException exception = assertThrows(ValidationException.class,
                () -> ItemRequestMapper.toItemRequestDto(itemRequest));
        assertThat("All itemRequest fields must be filled in.").isEqualTo(exception.getMessage());
    }

    @Test
    void toItemRequestDtoTest_createdIsNull() {
        itemRequest.setCreated(null);
        ValidationException exception = assertThrows(ValidationException.class,
                () -> ItemRequestMapper.toItemRequestDto(itemRequest));
        assertThat("All itemRequest fields must be filled in.").isEqualTo(exception.getMessage());
    }

    @Test
    void toItemRequestTest_Ok() {
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto);
        assertThat(itemRequestDto.getId()).isEqualTo(itemRequest.getId());
        assertThat(itemRequestDto.getRequester()).isEqualTo(itemRequest.getRequester());
    }

    @Test
    void toItemRequestTest_requesterIsNull() {
        itemRequestDto.setRequester(null);
        ValidationException exception = assertThrows(ValidationException.class,
                () -> ItemRequestMapper.toItemRequest(itemRequestDto));
        assertThat("All itemRequestDto fields must be filled in.").isEqualTo(
                exception.getMessage());
    }

    @Test
    void toItemRequestTest_descriptionIsNull() {
        itemRequestDto.setDescription(null);
        ValidationException exception = assertThrows(ValidationException.class,
                () -> ItemRequestMapper.toItemRequest(itemRequestDto));
        assertThat("All itemRequestDto fields must be filled in.").isEqualTo(
                exception.getMessage());
    }

    @Test
    void toItemRequestTest_createdIsNull() {
        itemRequestDto.setCreated(null);
        ValidationException exception = assertThrows(ValidationException.class,
                () -> ItemRequestMapper.toItemRequest(itemRequestDto));
        assertThat("All itemRequestDto fields must be filled in.").isEqualTo(
                exception.getMessage());
    }

    private ItemRequest createItemRequest(long id, User user) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(id);
        itemRequest.setRequester(user);
        itemRequest.setDescription("Description for item request " + id);
        itemRequest.setCreated(LocalDateTime.of(2023, 12, 1, 8, 0));
        return itemRequest;
    }

    private ItemRequestDto createItemRequestDto(long id, User user) {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(id);
        itemRequestDto.setRequester(user);
        itemRequestDto.setDescription("Description for item request " + id);
        itemRequestDto.setCreated(LocalDateTime.of(2023, 12, 1, 8, 0));
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