package ru.practicum.shareit.Feedback;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class Feedback {

    @NotNull
    private Long userId;
    @NotNull
    private Long itemId;
    @NotBlank
    private String feedback;

}
