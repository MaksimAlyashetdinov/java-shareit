package ru.practicum.shareit.item;

import java.util.Collections;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.shareit.item.dto.CommentDtoRequest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.valid.Marker;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {

    private final ItemClient itemClient;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    @Validated({Marker.OnCreate.class})
    public ResponseEntity<Object> createItem(@RequestHeader(USER_ID_HEADER) Long userId,
            @RequestBody @Valid ItemDto item) {
        log.info("Creating item={}, userId={}", item, userId);
        return itemClient.createItem(userId, item);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@RequestHeader(USER_ID_HEADER) Long userId,
            @PathVariable Long itemId) {
        log.info("Get item={}, userId={}", itemId, userId);
        return itemClient.getItem(userId, itemId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getByName(@RequestHeader(USER_ID_HEADER) Long userId,
            @RequestParam("text") String name,
            @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive
            @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Get items with text={}, userId={}, from={}, size={}", name, userId, from, size);
        if (name.isBlank()) {
            return ResponseEntity.ok(Collections.emptyList());
        }
        return itemClient.getItemsByName(name, userId, from, size);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItemsByUserId(@RequestHeader(USER_ID_HEADER) Long userId,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive
            @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Get items with userId={}, from={}, size={}", userId, from, size);
        return itemClient.getAllItemsByUserId(userId, from, size);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader(USER_ID_HEADER) Long userId,
            @PathVariable Long itemId, @Valid @RequestBody ItemDto item) {
        log.info("Update item={} from userId={}: item={}", itemId, userId, item);
        return itemClient.updateItem(userId, itemId, item);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Object> deleteItem(@PathVariable Long itemId) {
        log.info("Delete item={}", itemId);
        return itemClient.deleteItem(itemId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addCommentToItem(@RequestHeader(USER_ID_HEADER) Long userId,
            @PathVariable Long itemId, @RequestBody CommentDtoRequest comment) {
        log.info("Creating comment={}, for item={} from userId={}", comment, itemId, userId);
        return itemClient.addComment(userId, itemId, comment);
    }
}