package ru.practicum.shareit.request.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;

@Data
public class ItemRequestDto {

    private Long id;
    private String description;
    private User requester;
    private LocalDateTime created;
    private List<ItemDto> items;
}