package ru.practicum.shareit.item.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.storage.BookingStorage;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.item.CommentRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.storage.UserStorage;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;
    private final UserStorage userStorage;
    private final BookingStorage bookingStorage;
    private final CommentRepository commentRepository;

    @Override
    public Item createItem(Long userId, Item item) {
        containsUser(userId);
        validateItem(item);
        item.setOwnerId(userId);
        log.info("Item successfully added: " + item);
        return itemStorage.save(item);
    }

    @Override
    public Item getById(Long itemId) {
        Item item = itemStorage.findById(itemId).orElseThrow(() -> new NotFoundException("Item not found."));
        log.info("Requested item with ID = " + itemId);
        return item;
    }

    @Override
    public List<Item> getByName(String name) {
        if (name.isBlank()) {
            log.info("Get list with empty items name.");
            return new ArrayList<>();
        }
        List<Item> items = itemStorage.findAllByName(name);
        log.info("List of all items with name {}: " + items.size(),
                name);
        return items;
    }

    @Override
    public List<Item> getAllItemsByUserId(Long userId) {
        containsUser(userId);
        List<Item> items = itemStorage.findAllByOwnerId(userId);
        log.info(
                "List of all items for user with id {}: " + items.size(), userId);
        return items;
    }

    @Override
    public Item updateItem(Long userId, Long itemId, Item item) {
        containsUser(userId);
        Item itemFromStorage = itemStorage.findById(itemId).orElseThrow(() -> new NotFoundException("Item not found."));
        if (itemFromStorage.getOwnerId() != userId) {
            throw new NotFoundException(
                    "This item can update only user with id = " + itemFromStorage.getOwnerId());
        }
        if (item.getName() != null && !item.getName().isBlank()) {
            itemFromStorage.setName(item.getName());
        }
        if (item.getDescription() != null && !item.getDescription().isBlank()) {
            itemFromStorage.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            itemFromStorage.setAvailable(item.getAvailable());
        }
        log.info("Item successfully updated: " + itemFromStorage);
        return itemStorage.save(itemFromStorage);
    }

    @Override
    public void deleteItem(Long itemId) {
        Item item = itemStorage.findById(itemId).orElseThrow(() -> new NotFoundException("Item not found."));
        log.info("Deleted item with id: {}", itemId);
        itemStorage.delete(item);
    }

    @Override
    public Comment addCommentToItem(Long userId, Long itemId, String text) {
        containsUser(userId);
        Item item = itemStorage.findById(itemId).orElseThrow(() -> new NotFoundException("Item not found."));
        if (!bookingStorage.findAllByUserId(userId).stream().anyMatch(booking -> booking.getItem().equals(item))) {
            throw new ValidationException("This user can't add comment to this item.");
        }
        Comment comment = new Comment();
        comment.setUserId(userId);
        comment.setItemId(itemId);
        comment.setText(text);
        comment.setCreateDate(LocalDate.now());
        commentRepository.save(comment);
        return comment;
    }

    private void containsUser(Long id) {
        if (userStorage.findById(id).isEmpty()) {
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
    private Item makeItem(long id) {
        Item item = itemStorage.findById(id).orElseThrow(() -> new NotFoundException("Item not found."));
        item.setComments(commentRepository.findAllByItemId(id));
        return item;
    }
}