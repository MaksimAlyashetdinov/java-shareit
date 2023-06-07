package ru.practicum.shareit.request.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.Marker;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestDto {

    private Long id;
    @NotBlank
    @Size(max = 200, groups = {Marker.OnCreate.class, Marker.OnUpdate.class})
    private String description;
}