package ru.practicum.shareit.request;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestService itemRequestService;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ItemRequestDto create(@RequestBody ItemRequestDto itemRequestDto,
            @RequestHeader(USER_ID_HEADER) Long userId) {
        return itemRequestService.create(itemRequestDto, userId, LocalDateTime.now());
    }

    @GetMapping(value = "/{requestId}")
    public ItemRequestDto getById(@PathVariable(value = "requestId") Long requestId,
            @RequestHeader(USER_ID_HEADER) Long userId) {
        return itemRequestService.getById(requestId, userId);
    }

    @GetMapping
    public List<ItemRequestDto> getAllByUser(@RequestHeader(USER_ID_HEADER) Long userId) {
        return itemRequestService.getAllByUser(userId);
    }

    @GetMapping(value = "/all")
    public List<ItemRequestDto> getAll(
            @RequestParam(
                    name = "from",
                    defaultValue = "0") int from,
            @RequestParam(
                    name = "size",
                    required = false,
                    defaultValue = "10") int size,
            @RequestHeader(USER_ID_HEADER) Long userId) {
        return itemRequestService.getAll(userId, from, size);
    }
}