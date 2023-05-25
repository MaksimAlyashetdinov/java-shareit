package ru.practicum.shareit.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

    @Test
    void getAllTest() {
        User user = createUser(1);
        Mockito.when(userRepository.findAll()).thenReturn(List.of(user));
        List<User> usersFromService = userService.getAll();
        assertEquals(1, usersFromService.size());
    }

    @Test
    void createTest_NameNull() {
        User user = createUser(1);
        user.setName(null);
        ValidationException exception = assertThrows(ValidationException.class,
                () -> userService.create(user));
        assertEquals("You must specify the name and email.", exception.getMessage());
    }

    @Test
    void createTest_EmailNull() {
        User user = createUser(1);
        user.setEmail(null);
        ValidationException exception = assertThrows(ValidationException.class,
                () -> userService.create(user));
        assertEquals("You must specify the name and email.", exception.getMessage());
    }

    @Test
    void createTest_Ok() {
        User user = createUser(1);
        Mockito.when(userRepository.save(user)).thenReturn(user);
        User userFromService = userService.create(user);
        assertEquals(user.getId(), userFromService.getId());
        assertEquals(user.getName(), userFromService.getName());
    }

    @Test
    void updateTest_UserNotFound() {
        User user = createUser(1);
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> userService.update(2, user));
        assertEquals("User with not found.", exception.getMessage());
    }

    @Test
    void updateTest_Ok() {
        User user = createUser(1);
        User updateUser = user;
        updateUser.setName("Update name");
        updateUser.setEmail("update@email.ru");
        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        Mockito.when(userRepository.save(updateUser)).thenReturn(updateUser);
        User userFromService = userService.update(user.getId(), user);
        assertEquals(user.getName(), userFromService.getName());
        assertEquals(user.getEmail(), userFromService.getEmail());
    }

    @Test
    void deleteTest_userNotFound() {
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> userService.delete(2));
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void deleteTest_Ok() {
        User user = createUser(1);
        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        userService.delete(user.getId());
    }

    @Test
    void getByIdTest_UserNotFound() {
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> userService.getById(Mockito.anyLong()));
        assertEquals("User not found.", exception.getMessage());
    }

    @Test
    void getByIdTest_Ok() {
        User user = createUser(1);
        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        User userFromService = userService.getById(user.getId());
        assertEquals(user.getId(), userFromService.getId());
    }

    private User createUser(long id) {
        User user = new User();
        user.setId(id);
        user.setName("User " + id);
        user.setEmail("user_" + id + "@email.ru");
        return user;
    }
}