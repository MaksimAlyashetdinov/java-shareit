package ru.practicum.shareit.user;

import java.util.Collection;
import javax.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.user.service.UserService;

@RestController
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public User create(@RequestBody @Valid User user) {
        return userService.create(user);
    }

    @GetMapping
    public Collection<User> getAll() {
        return userService.getAll();
    }

    @PatchMapping("/{userId}")
    public User update(@PathVariable Long userId, @RequestBody @Valid User user) {
        return userService.update(userId, user);
    }

    @DeleteMapping("/{userId}")
    public User delete(@PathVariable Long userId) {
        return userService.delete(userId);
    }

    @GetMapping("/{userId}")
    public User getById(@PathVariable Long userId) {
        return userService.getById(userId);
    }
}