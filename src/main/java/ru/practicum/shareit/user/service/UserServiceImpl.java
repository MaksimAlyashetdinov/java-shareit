package ru.practicum.shareit.user.service;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ObjectAlreadyExistsException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.storage.UserDbStorage;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserDbStorage userDbStorage;

    public UserServiceImpl(UserDbStorage userDbStorage) {
        this.userDbStorage = userDbStorage;
    }

    @Override
    public List<User> getAll() {
        log.info("List of all users: " + userDbStorage.getAll().size());
        return userDbStorage.getAll();
    }

    @Override
    public User create(User user) {
        validate(user);
        containsEmail(user.getEmail());
        log.info("User successfully added: " + user);
        return userDbStorage.create(user);
    }

    @Override
    public User update(Long userId, User user) {
        containsUser(userId);
        User userFromBd = userDbStorage.getById(userId);
        if (!userFromBd.getEmail().equals(user.getEmail())) {
            containsEmail(user.getEmail());
        }
        log.info("User successfully updated: " + user);
        return userDbStorage.update(userId, user);
    }

    @Override
    public User delete(Long id) {
        containsUser(id);
        log.info("Deleted user with id: {}", id);
        return userDbStorage.delete(id);
    }

    @Override
    public User getById(Long id) {
        containsUser(id);
        log.info("Requested user with ID = " + id);
        return userDbStorage.getById(id);
    }

    @Override
    public void containsUser(Long id) {
        if (!userDbStorage.containsUser(id)) {
            throw new NotFoundException("User with id = " + id + " not exist.");
        }
    }

    @Override
    public void containsEmail(String email) {
        if (userDbStorage.containsEmail(email)) {
            throw new ObjectAlreadyExistsException(
                    "User with email = " + email + " already exists.");
        }
    }

    private void validate(User user) {
        if (user.getName() == null
                || user.getEmail() == null) {
            throw new ValidationException("You must specify the name and email.");
        }
    }
}
