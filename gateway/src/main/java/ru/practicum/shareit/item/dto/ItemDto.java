package ru.practicum.shareit.item.dto;

import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {

    @Size(max = 255)
    private String name;
    @Size(max = 200)
    private String description;
    private Boolean available;
    private Long requestId;
}