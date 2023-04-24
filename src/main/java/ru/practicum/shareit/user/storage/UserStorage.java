package ru.practicum.shareit.user.storage;

import java.util.List;
import ru.practicum.shareit.user.User;

public interface UserStorage {
    List<User> getAll();

    User create(User user);

    User update(Long userId, User user);

    User delete(Long id);

    User getById(Long id);

    boolean containsUser(Long id);

    boolean containsEmail(String email);
}
