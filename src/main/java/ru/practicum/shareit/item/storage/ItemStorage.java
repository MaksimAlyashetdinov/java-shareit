package ru.practicum.shareit.item.storage;

import java.util.List;
import ru.practicum.shareit.item.model.Item;

public interface ItemStorage {

    List<Item> getAllByName(String name);

    Item create(Item item);

    Item update(Long itemId, Item item);

    void delete(Long id);

    Item getById(Long id);

    List<Item> getAllItemsByUserId(Long userId);

    boolean containsItem(Long id);
}
