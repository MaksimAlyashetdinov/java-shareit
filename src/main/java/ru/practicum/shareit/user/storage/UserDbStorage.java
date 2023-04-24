package ru.practicum.shareit.user.storage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;

@Repository
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<User> getAll() {
        String sqlQuery = "SELECT * FROM users";
        SqlRowSet srs = jdbcTemplate.queryForRowSet(sqlQuery);
        List<User> users = new ArrayList<>();
        while (srs.next()) {
            users.add(userMap(srs));
        }
        return users;
    }

    @Override
    public User create(User user) {
        Map<String, Object> keys = new SimpleJdbcInsert(this.jdbcTemplate)
                .withTableName("users")
                .usingColumns("user_name", "email")
                .usingGeneratedKeyColumns("user_id")
                .executeAndReturnKeyHolder(Map.of(
                        "user_name", user.getName(),
                        "email", user.getEmail()))
                .getKeys();
        user.setId((Long) keys.get("user_id"));
        return user;
    }

    @Override
    public User update(Long userId, User user) {
        if (user.getName() == null) {
            String sqlQuery = "UPDATE users "
                    + "SET email = ? "
                    + "WHERE user_id = ?";
            jdbcTemplate.update(sqlQuery, user.getEmail(), userId);
        }
        if (user.getEmail() == null) {
            String sqlQuery = "UPDATE users "
                    + "SET user_name = ? "
                    + "WHERE user_id = ?";
            jdbcTemplate.update(sqlQuery, user.getName(), userId);
        }
        if (user.getName() != null && user.getEmail() != null) {
            String sqlQuery = "UPDATE users "
                    + "SET user_name = ?, "
                    + "email = ? "
                    + "WHERE user_id = ?";
            jdbcTemplate.update(sqlQuery, user.getName(), user.getEmail(), userId);
        }
        return getById(userId);
    }

    @Override
    public User delete(Long id) {
        User user = getById(id);
        jdbcTemplate.execute("DELETE FROM users WHERE user_id = " + id);
        return user;
    }

    @Override
    public User getById(Long id) {
        String sqlQuery = "SELECT * FROM users WHERE user_id = ?";
        SqlRowSet srs = jdbcTemplate.queryForRowSet(sqlQuery, id);
        if (srs.next()) {
            return userMap(srs);
        } else {
            throw new NotFoundException("User with ID = " + id + " not found!");
        }
    }

    @Override
    public boolean containsUser(Long id) {
        return jdbcTemplate.queryForRowSet("SELECT user_id FROM users WHERE user_id=?", id).next();
    }

    @Override
    public boolean containsEmail(String email) {
        return jdbcTemplate.queryForRowSet("SELECT user_id FROM users WHERE email=?", email).next();
    }

    private User userMap(SqlRowSet srs) {
        Long id = srs.getLong("user_id");
        return User.builder()
                .id(id)
                .name(srs.getString("user_name"))
                .email(srs.getString("email"))
                .build();
    }
}
