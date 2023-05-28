package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.User;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto extends User {

    private Long id;
    private String name;
    private String email;
}