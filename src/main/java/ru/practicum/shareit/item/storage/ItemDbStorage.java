package ru.practicum.shareit.item.storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;

@Repository
public class ItemDbStorage implements ItemStorage {

    Map<Long, Item> items = new HashMap<>();
    private long id;

    @Override
    public List<Item> getAllByName(String name) {
        return new ArrayList<>(items
                .values()
                .stream()
                .filter(item -> item.getName().toLowerCase().contains(name.toLowerCase())
                        || item.getDescription().toLowerCase().contains(name.toLowerCase()))
                .filter(item -> item.getAvailable() == true)
                .collect(Collectors.toList()));
    }

    @Override
    public Item create(Item item) {
        item.setId(nextId());
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item update(Long itemId, Item item) {
        Item itemFromMemory = items.get(itemId);
        if (item.getName() != null && !item.getName().isBlank()) {
            itemFromMemory.setName(item.getName());
        }
        if (item.getDescription() != null && !item.getDescription().isBlank()) {
            itemFromMemory.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            itemFromMemory.setAvailable(item.getAvailable());
        }
        items.put(itemId, itemFromMemory);
        return items.get(itemId);
    }

    @Override
    public void delete(Long id) {
        items.remove(id);
    }

    @Override
    public Item getById(Long id) {
        if (items.containsKey(id)) {
            return items.get(id);
        } else {
            throw new NotFoundException("Item with ID = " + id + " not found");
        }
    }

    @Override
    public List<Item> getAllItemsByUserId(Long userId) {
        return new ArrayList<>(items
                .values()
                .stream()
                .filter(item -> item.getOwnerId().equals(userId))
                .collect(Collectors.toList()));
    }

    @Override
    public boolean containsItem(Long id) {
        return items.containsKey(id);
    }

    private long nextId() {
        return ++id;
    }
}
