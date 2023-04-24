package ru.practicum.shareit.user.service;

import java.util.List;
import ru.practicum.shareit.user.User;

public interface UserService {
    List<User> getAll();

    User create(User user);

    User update(Long userId, User user);

    User delete(Long id);

    User getById(Long id);

    void containsUser(Long id);
    void containsEmail(String email);
}
