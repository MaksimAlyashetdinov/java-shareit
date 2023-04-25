package ru.practicum.shareit.user.service;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ObjectAlreadyExistsException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.storage.InMemoryUserStorage;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final InMemoryUserStorage inMemoryUserStorage;

    public UserServiceImpl(InMemoryUserStorage inMemoryUserStorage) {
        this.inMemoryUserStorage = inMemoryUserStorage;
    }

    @Override
    public List<User> getAll() {
        log.info("List of all users: " + inMemoryUserStorage.getAll().size());
        return inMemoryUserStorage.getAll();
    }

    @Override
    public User create(User user) {
        validate(user);
        containsEmail(user.getEmail());
        log.info("User successfully added: " + user);
        return inMemoryUserStorage.create(user);
    }

    @Override
    public User update(Long userId, User user) {
        containsUser(userId);
        User userFromMemory = inMemoryUserStorage.getById(userId);
        if (!userFromMemory.getEmail().equals(user.getEmail())) {
            containsEmail(user.getEmail());
        }
        if (user.getName() != null && !user.getName().isBlank()) {
            userFromMemory.setName(user.getName());
        }
        if (user.getEmail() != null) {
            userFromMemory.setEmail(user.getEmail());
        }
        log.info("User successfully updated: " + userFromMemory);
        return inMemoryUserStorage.update(userId, userFromMemory);
    }

    @Override
    public void delete(Long id) {
        containsUser(id);
        log.info("Deleted user with id: {}", id);
        inMemoryUserStorage.delete(id);
    }

    @Override
    public User getById(Long id) {
        containsUser(id);
        log.info("Requested user with ID = " + id);
        return inMemoryUserStorage.getById(id);
    }

    @Override
    public void containsUser(Long id) {
        if (!inMemoryUserStorage.containsUser(id)) {
            throw new NotFoundException("User with id = " + id + " not exist.");
        }
    }

    @Override
    public void containsEmail(String email) {
        if (inMemoryUserStorage.containsEmail(email)) {
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
