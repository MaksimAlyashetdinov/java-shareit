package ru.practicum.shareit.item.service;

import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.storage.UserStorage;

@Service
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    public ItemServiceImpl(ItemStorage itemStorage,
            UserStorage userStorage) {
        this.itemStorage = itemStorage;
        this.userStorage = userStorage;
    }

    @Override
    public Item createItem(Long userId, Item item) {
        containsUser(userId);
        validateItem(item);
        item.setOwnerId(userId);
        log.info("Item successfully added: " + item);
        return itemStorage.create(item);
    }

    @Override
    public Item getById(Long itemId) {
        containsItem(itemId);
        log.info("Requested item with ID = " + itemId);
        return itemStorage.getById(itemId);
    }

    @Override
    public List<Item> getByName(String name) {
        if (name.isBlank()) {
            log.info("Get list with empty items name.");
            return new ArrayList<>();
        }
        log.info("List of all items with name {}: " + itemStorage.getAllByName(name).size(),
                name);
        return itemStorage.getAllByName(name);
    }

    @Override
    public List<Item> getAllItemsByUserId(Long userId) {
        containsUser(userId);
        log.info(
                "List of all items for user with id {}: " + itemStorage.getAllItemsByUserId(
                        userId).size(), userId);
        return itemStorage.getAllItemsByUserId(userId);
    }

    @Override
    public Item updateItem(Long userId, Long itemId, Item item) {
        containsUser(userId);
        containsItem(itemId);
        validateUpdateItem(userId, itemId);
        Item itemFromMemory = getById(itemId);
        if (item.getName() != null && !item.getName().isBlank()) {
            itemFromMemory.setName(item.getName());
        }
        if (item.getDescription() != null && !item.getDescription().isBlank()) {
            itemFromMemory.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            itemFromMemory.setAvailable(item.getAvailable());
        }
        log.info("Item successfully updated: " + itemFromMemory);
        return itemStorage.update(itemId, itemFromMemory);
    }

    @Override
    public void deleteItem(Long itemId) {
        containsItem(itemId);
        log.info("Deleted item with id: {}", itemId);
        itemStorage.delete(itemId);
    }

    private void containsItem(Long id) {
        if (!itemStorage.containsItem(id)) {
            throw new NotFoundException("Item with id = " + id + " not exist.");
        }
    }

    private void containsUser(Long id) {
        if (!userStorage.containsUser(id)) {
            throw new NotFoundException("User with id = " + id + " not exist.");
        }
    }

    private void validateItem(Item item) {
        if (item.getName() == null || item.getName().isBlank()) {
            throw new ValidationException("You must specify the name.");
        }
        if (item.getDescription() == null || item.getDescription().isBlank()) {
            throw new ValidationException("You must specify the description.");
        }
        if (item.getAvailable() == null) {
            throw new ValidationException("You must specify the available.");
        }
    }

    private void validateUpdateItem(long userId, long itemId) {
        Item itemFromMemory = itemStorage.getById(itemId);
        if (itemFromMemory.getOwnerId() != userId) {
            throw new NotFoundException(
                    "This item can update only user with id = " + itemFromMemory.getOwnerId());
        }
    }
}