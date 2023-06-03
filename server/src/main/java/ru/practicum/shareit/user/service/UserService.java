package ru.practicum.shareit.user.service;

import java.util.List;
import ru.practicum.shareit.user.User;

public interface UserService {

    List<User> getAll();

    User create(User user);

    User update(long userId, User user);

    User delete(long id);

    User getById(long id);
}