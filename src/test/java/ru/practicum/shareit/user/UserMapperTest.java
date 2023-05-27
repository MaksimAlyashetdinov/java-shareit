package ru.practicum.shareit.user;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.utils.UserMapper;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
public class UserMapperTest {

    private User user;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        user = createUser(new User(), 1);
        userDto = createUser(new UserDto(), 1);
    }

    @Test
    void mapToUserDtoTest_Ok() {
        UserDto userDto = UserMapper.mapToUserDto(user);
        assertThat(user.getId()).isEqualTo(userDto.getId());
        assertThat(user.getName()).isEqualTo(userDto.getName());
    }

    @Test
    void mapToUserDtoTest_idIsNull() {
        user.setId(null);
        ValidationException exception = assertThrows(ValidationException.class,
                () -> UserMapper.mapToUserDto(user));
        assertThat("All user fields must be filled in.").isEqualTo(exception.getMessage());
    }

    @Test
    void mapToUserDtoTest_nameIsNull() {
        user.setName(null);
        ValidationException exception = assertThrows(ValidationException.class,
                () -> UserMapper.mapToUserDto(user));
        assertThat("All user fields must be filled in.").isEqualTo(exception.getMessage());
    }

    @Test
    void mapToUserDtoTest_emailIsNull() {
        user.setEmail(null);
        ValidationException exception = assertThrows(ValidationException.class,
                () -> UserMapper.mapToUserDto(user));
        assertThat("All user fields must be filled in.").isEqualTo(exception.getMessage());
    }

    @Test
    void mapToUserTest_Ok() {
        User user = UserMapper.mapToUser(userDto);
        assertThat(user.getId()).isEqualTo(userDto.getId());
        assertThat(user.getName()).isEqualTo(userDto.getName());
    }

    @Test
    void mapToUserTest_idIsNull() {
        userDto.setId(null);
        ValidationException exception = assertThrows(ValidationException.class,
                () -> UserMapper.mapToUser(userDto));
        assertThat("All userDto fields must be filled in.").isEqualTo(exception.getMessage());
    }

    @Test
    void mapToUserTest_nameIsNull() {
        userDto.setName(null);
        ValidationException exception = assertThrows(ValidationException.class,
                () -> UserMapper.mapToUser(userDto));
        assertThat("All userDto fields must be filled in.").isEqualTo(exception.getMessage());
    }

    @Test
    void mapToUserTest_emailIsNull() {
        userDto.setEmail(null);
        ValidationException exception = assertThrows(ValidationException.class,
                () -> UserMapper.mapToUser(userDto));
        assertThat("All userDto fields must be filled in.").isEqualTo(exception.getMessage());
    }

    private <T extends User> T createUser(T us, long id) {
        us.setId(id);
        us.setName("User " + id);
        us.setEmail("user_" + id + "@email.ru");
        return us;
    }
}