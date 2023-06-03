package ru.practicum.shareit.request.service;

import java.time.LocalDateTime;
import java.util.List;
import ru.practicum.shareit.request.dto.ItemRequestDto;

public interface ItemRequestService {

    ItemRequestDto create(ItemRequestDto requestDto, Long userId, LocalDateTime created);

    ItemRequestDto getById(Long requestId, Long userId);

    List<ItemRequestDto> getAllByUser(Long userId);

    List<ItemRequestDto> getAll(Long userId, Integer from, Integer size);
}