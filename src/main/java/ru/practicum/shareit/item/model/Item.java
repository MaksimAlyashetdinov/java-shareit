package ru.practicum.shareit.item.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import jdk.jfr.BooleanFlag;
import lombok.Data;

/**
 * TODO Sprint add-controllers.
 */

@Data
public class Item {
    private Long id;
    @NotBlank
    private String title;
    @NotBlank
    private String description;
    @NotNull
    private Boolean available;
    private Long ownerId;
}
