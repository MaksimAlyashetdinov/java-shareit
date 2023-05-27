package ru.practicum.shareit.user;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
import ru.practicum.shareit.user.service.UserServiceImpl;
import ru.practicum.shareit.user.storage.UserRepository;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @InjectMocks
    UserServiceImpl userService;

    @Mock
    UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        user = createUser(1);
    }

    @Test
    void getAllTest() {
        Mockito.when(userRepository.findAll())
               .thenReturn(List.of(user));
        List<User> usersFromService = userService.getAll();
        assertThat(1).isEqualTo(usersFromService.size());
    }

    @Test
    void createTest_NameNull() {
        user.setName(null);
        ValidationException exception = assertThrows(ValidationException.class,
                () -> userService.create(user));
        assertThat("You must specify the name and email.").isEqualTo(exception.getMessage());
    }

    @Test
    void createTest_EmailNull() {
        user.setEmail(null);
        ValidationException exception = assertThrows(ValidationException.class,
                () -> userService.create(user));
        assertThat("You must specify the name and email.").isEqualTo(exception.getMessage());
    }

    @Test
    void createTest_Ok() {
        Mockito.when(userRepository.save(user))
               .thenReturn(user);
        User userFromService = userService.create(user);
        assertThat(user.getId()).isEqualTo(userFromService.getId());
        assertThat(user.getName()).isEqualTo(userFromService.getName());
    }

    @Test
    void updateTest_UserNotFound() {
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> userService.update(2, user));
        assertThat("User with not found.").isEqualTo(exception.getMessage());
    }

    @Test
    void updateTest_Ok() {
        User updateUser = user;
        updateUser.setName("Update name");
        updateUser.setEmail("update@email.ru");
        Mockito.when(userRepository.findById(user.getId()))
               .thenReturn(Optional.of(user));
        Mockito.when(userRepository.save(updateUser))
               .thenReturn(updateUser);
        User userFromService = userService.update(user.getId(), user);
        assertThat(user.getName()).isEqualTo(userFromService.getName());
        assertThat(user.getEmail()).isEqualTo(userFromService.getEmail());
    }

    @Test
    void deleteTest_userNotFound() {
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> userService.delete(2));
        assertThat("User not found").isEqualTo(exception.getMessage());
    }

    @Test
    void deleteTest_Ok() {
        Mockito.when(userRepository.findById(user.getId()))
               .thenReturn(Optional.of(user));
        userService.delete(user.getId());
    }

    @Test
    void getByIdTest_UserNotFound() {
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> userService.getById(Mockito.anyLong()));
        assertThat("User not found.").isEqualTo(exception.getMessage());
    }

    @Test
    void getByIdTest_Ok() {
        Mockito.when(userRepository.findById(user.getId()))
               .thenReturn(Optional.of(user));
        User userFromService = userService.getById(user.getId());
        assertThat(user.getId()).isEqualTo(userFromService.getId());
    }

    private User createUser(long id) {
        User user = new User();
        user.setId(id);
        user.setName("User " + id);
        user.setEmail("user_" + id + "@email.ru");
        return user;
    }
}