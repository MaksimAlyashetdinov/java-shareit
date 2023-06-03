package ru.practicum.shareit.request.dto;

import java.time.LocalDateTime;
import java.util.List;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemDto;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestDto{

    private Long id;
    @NotNull
    private String description;
    private Long requester;
    List<ItemDto> items;
    @FutureOrPresent
    LocalDateTime created;
}