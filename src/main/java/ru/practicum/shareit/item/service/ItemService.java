package ru.practicum.shareit.item.service;

import java.util.List;
import ru.practicum.shareit.item.model.Item;

public interface ItemService {
    Item createItem(Long userId, Item item);
    Item getById(Long itemId);
    List<Item> getByName(String title);
    List<Item> getAllItemsByUserId(Long userId);
    Item updateItem(Long userId, Long itemId, Item item);
    Item deleteItem(Long itemId);
}
