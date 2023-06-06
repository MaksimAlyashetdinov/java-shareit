package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.valid.StartBeforeEndDateValid;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BookItemRequestDto {

    @NotNull
    private Long itemId;

    @StartBeforeEndDateValid
    private LocalDateTime start;

    @StartBeforeEndDateValid
    private LocalDateTime end;
}