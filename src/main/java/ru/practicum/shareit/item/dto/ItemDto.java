package ru.practicum.shareit.item.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 * TODO Sprint add-controllers.
 */

@Data
public class ItemDto {
    private Long id;
    private String title;
    private String description;
    private Boolean available;
    private Long ownerId;
}
