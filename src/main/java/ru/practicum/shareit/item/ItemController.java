package ru.practicum.shareit.item;

import java.util.List;
import javax.validation.Valid;
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

    @PostMapping
    public Item createItem(@RequestHeader("X-Sharer-User-Id") Long userId,
            @Valid @RequestBody Item item) {
        return itemService.createItem(userId, item);
    }

    @GetMapping("/{itemId}")
    public ItemDto getById(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId) {
        return itemService.getById(userId, itemId);
    }

    @GetMapping("/search")
    public List<Item> getByName(@RequestParam("text") String name) {
        return itemService.getByName(name);
    }

    @GetMapping
    public List<ItemDto> getAllItemsByUserId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getAllItemsByUserId(userId);
    }

    @PatchMapping("/{itemId}")
    public Item updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long itemId, @Valid @RequestBody Item item) {
        return itemService.updateItem(userId, itemId, item);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@PathVariable Long itemId) {
        itemService.deleteItem(itemId);
    }

    @PostMapping("/{itemId}/comment")
        public CommentDto addCommentToItem(@RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long itemId, @RequestBody CommentDtoRequest comment) {
        long user = userId;
        long item = itemId;
        CommentDtoRequest commentDtoRequest = comment;
        return itemService.addCommentToItem(userId, itemId, comment);
    }
}
