package ru.practicum.shareit.user;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.utils.UserMapper;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
public class UserMapperTest {

    @Test
    void mapToUserDtoTest() {
        User user = createUser(1);
        UserDto userDto = UserMapper.mapToUserDto(user);
        assertEquals(user.getId(), userDto.getId());
        assertEquals(user.getName(), userDto.getName());
    }

    @Test
    void mapToUserTest() {
        UserDto userDto = createUserDto(1);
        User user = UserMapper.mapToUser(userDto);
        assertEquals(user.getId(), userDto.getId());
        assertEquals(user.getName(), userDto.getName());
    }

    private User createUser(long id) {
        User user = new User();
        user.setId(id);
        user.setName("User " + id);
        user.setEmail("user_" + id + "@email.ru");
        return user;
    }

    private UserDto createUserDto(long id) {
        UserDto userDto = new UserDto();
        userDto.setId(id);
        userDto.setName("Name for user " + id);
        userDto.setEmail("user" + id + "@email.ru");
        return userDto;
    }
}