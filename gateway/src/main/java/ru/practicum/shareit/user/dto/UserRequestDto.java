package ru.practicum.shareit.user.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.Marker;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestDto {

    @NotBlank(groups = Marker.OnCreate.class)
    @Size(max = 255)
    private String name;
    @NotBlank(groups = Marker.OnCreate.class)
    @Email
    @Size(max = 255)
    private String email;
}