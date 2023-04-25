package ru.practicum.shareit.user.storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;

@Repository
public class InMemoryUserStorage implements UserStorage {

    private Map<Long, User> users = new HashMap<>();
    private long id;

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User create(User user) {
        user.setId(nextId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(Long userId, User user) {
        users.put(userId, user);
        return user;
    }

    @Override
    public void delete(Long id) {
        users.remove(id);
    }

    @Override
    public User getById(Long id) {
        if (users.containsKey(id)) {
            return users.get(id);
        } else {
            throw new NotFoundException("User with ID = " + id + " not found!");
        }
    }

    @Override
    public boolean containsUser(Long id) {
        return users.containsKey(id);
    }

    @Override
    public boolean containsEmail(String email) {
        List<String> usersEmail = new ArrayList<>();
        users.forEach((key, user) -> usersEmail.add(user.getEmail()));
        return usersEmail.contains(email);
    }

    private long nextId() {
        return ++id;
    }
}
