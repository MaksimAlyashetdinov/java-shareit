package ru.practicum.shareit.user.utils;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

@Component
public class UserMapper {

    public static UserDto mapToUserDto(User user) {
        if (user.getId() == null || user.getName() == null || user.getEmail() == null) {
            throw new ValidationException("All user fields must be filled in.");
        }
        return new UserDto(user.getId(), user.getName(), user.getEmail());
    }

    public static User mapToUser(UserDto dto) {
        if (dto.getId() == null || dto.getName() == null || dto.getEmail() == null) {
            throw new ValidationException("All userDto fields must be filled in.");
        }
        User user = new User();
        user.setId(dto.getId());
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        return user;
    }
}