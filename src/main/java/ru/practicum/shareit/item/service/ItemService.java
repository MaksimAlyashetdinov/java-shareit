package ru.practicum.shareit.item.service;

import java.util.List;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

public interface ItemService {

    Item createItem(Long userId, Item item);

    ItemDto getById(Long userId, Long itemId);

    List<Item> getByName(String title);

    List<ItemDto> getAllItemsByUserId(Long userId);

    Item updateItem(Long userId, Long itemId, Item item);

    void deleteItem(Long itemId);

    Comment addCommentToItem(Long userId, Long itemId, String text);
}
