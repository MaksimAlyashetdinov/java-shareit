package ru.practicum.shareit.user.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.storage.UserRepository;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public List<User> getAll() {
        List<User> users = userRepository.findAll();
        log.info("Get all users: " + users);
        return users;
    }

    @Override
    public User create(User user) {
        validate(user);
        log.info("User successfully added: " + user);
        return userRepository.save(user);
    }

    @Override
    public User update(long userId, User user) {
        User userFromStorage = userRepository.findById(userId)
                                             .orElseThrow(() -> new NotFoundException(
                                                     "User with not found."));
        if (user.getName() != null && !user.getName()
                                           .isBlank()) {
            userFromStorage.setName(user.getName());
        }
        if (user.getEmail() != null) {
            userFromStorage.setEmail(user.getEmail());
        }
        log.info("User successfully updated: " + userFromStorage);
        return userRepository.save(userFromStorage);
    }

    @Override
    public void delete(long id) {
        User user = userRepository.findById(id)
                                  .orElseThrow(() -> new NotFoundException("User not found"));
        log.info("Deleted user with id: {}", id);
        userRepository.delete(user);
    }

    @Override
    public User getById(long id) {
        User user = userRepository.findById(id)
                                  .orElseThrow(() -> new NotFoundException("User not found."));
        log.info("Get user: " + user);
        return user;
    }

    private void validate(User user) {
        if (user.getName() == null
                || user.getEmail() == null) {
            throw new ValidationException("You must specify the name and email.");
        }
    }
}