package ru.practicum.shareit.item;

import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoRequest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

@RestController
@RequestMapping(path = "/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public Item createItem(@RequestHeader(USER_ID_HEADER) Long userId,
            @Valid @RequestBody Item item) {
        return itemService.createItem(userId, item);
    }

    @GetMapping("/{itemId}")
    public ItemDto getById(@RequestHeader(USER_ID_HEADER) Long userId,
            @PathVariable Long itemId) {
        return itemService.getById(userId, itemId);
    }

    @GetMapping("/search")
    public List<Item> getByName(@RequestParam("text") String name,
            @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive
            @RequestParam(name = "size", defaultValue = "10", required = false) Integer size) {
        return itemService.getByName(name, from, size);
    }

    @GetMapping
    public List<ItemDto> getAllItemsByUserId(@RequestHeader(USER_ID_HEADER) Long userId,
            @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive
            @RequestParam(name = "size", defaultValue = "10", required = false) Integer size) {
        return itemService.getAllItemsByUserId(userId, from, size);
    }

    @PatchMapping("/{itemId}")
    public Item updateItem(@RequestHeader(USER_ID_HEADER) Long userId,
            @PathVariable Long itemId, @Valid @RequestBody Item item) {
        return itemService.updateItem(userId, itemId, item);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@PathVariable Long itemId) {
        itemService.deleteItem(itemId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addCommentToItem(@RequestHeader(USER_ID_HEADER) Long userId,
            @PathVariable Long itemId, @RequestBody CommentDtoRequest comment) {
        CommentDtoRequest commentDtoRequest = comment;
        return itemService.addCommentToItem(userId, itemId, comment);
    }
}