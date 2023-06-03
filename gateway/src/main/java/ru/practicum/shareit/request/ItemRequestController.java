package ru.practicum.shareit.request;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.shareit.request.dto.ItemRequestDto;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> createRequest(@Valid @RequestBody ItemRequestDto itemRequestDto,
            @RequestHeader(USER_ID_HEADER) Long userId) {
        log.info("Creating create request={} from userId={}", itemRequestDto, userId);
        return itemRequestClient.createItemRequest(userId, itemRequestDto);
    }

    @GetMapping(value = "/{requestId}")
    public ResponseEntity<Object> getItemRequest(@PathVariable(value = "requestId") Long requestId,
            @RequestHeader(USER_ID_HEADER) Long userId) {
        log.info("Get request={}, userId={}", requestId, userId);
        return itemRequestClient.getItemRequest(requestId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllByUser(@RequestHeader(USER_ID_HEADER) Long userId,
            @PositiveOrZero
            @RequestParam(name = "from",defaultValue = "0") int from,
            @Positive @RequestParam(name = "size", defaultValue = "10") int size) {
        log.info("Get requests for userId={}, from={}, size={}", userId, from, size);
        return itemRequestClient.getAllItemRequestsByUserId(userId, from, size);
    }

    @GetMapping(value = "/all")
    public ResponseEntity<Object> getAll(@PositiveOrZero
    @RequestParam(name = "from", defaultValue = "0") int from,
            @Positive @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestHeader(USER_ID_HEADER) Long userId) {
        log.info("Get all requests for userId={}, from={}, size={}", userId, from, size);
        return itemRequestClient.getAllItemRequests(userId, from, size);
    }
}