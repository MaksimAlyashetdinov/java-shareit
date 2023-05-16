package ru.practicum.shareit.item.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.storage.BookingStorage;
import ru.practicum.shareit.booking.utils.BookingMapper;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoRequest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.item.utils.CommentMapper;
import ru.practicum.shareit.item.utils.ItemMapper;
import ru.practicum.shareit.user.User;
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
    private final ItemMapper itemMapper;
    private final BookingMapper bookingMapper;
    private final CommentMapper commentMapper;

    @Override
    public Item createItem(Long userId, Item item) {
        validateItem(item);
        containsUser(userId);
        item.setOwnerId(userId);
        log.info("Item successfully added: " + item);
        return itemStorage.save(item);
    }

    @Override
    public ItemDto getById(Long userId, Long itemId) {
        Item item = containsItem(itemId);
        List<Comment> comments = commentRepository.findAllByItemId(item.getId());
        List<CommentDto> commentsDto = null;
        if (comments.size() > 0) {
            commentsDto = comments.stream().map(comment -> commentMapper.toCommentDto(comment)).collect(
                    Collectors.toList());
        }
        if (userId == item.getOwnerId()) {
            List<Booking> bookings = bookingStorage.findAllByItemId(itemId);
            Optional<Booking> lastBooking = bookings.stream()
                                                    .filter(booking -> booking.getStart()
                                                                              .isBefore(
                                                                                      LocalDateTime.now()))
                                                    .filter(booking -> !booking.getStatus()
                                                                               .equals(BookingState.REJECTED))
                                                    .sorted((b1, b2) -> b2.getStart()
                                                                          .compareTo(b1.getStart()))
                                                    .findFirst();
            BookingDto lastBookingDto = null;
            if (lastBooking.isPresent()) {
                lastBookingDto = bookingMapper.mapToBookingDto(lastBooking.get());
            }
            Optional<Booking> nextBooking = bookings.stream()
                                                    .filter(booking -> booking.getStart()
                                                                              .isAfter(
                                                                                      LocalDateTime.now()))
                                                    .filter(booking -> !booking.getStatus()
                                                                               .equals(BookingState.REJECTED))
                                                    .sorted((b1, b2) -> b1.getStart()
                                                                          .compareTo(b2.getStart()))
                                                    .findFirst();
            BookingDto nextBookingDto = null;
            if (nextBooking.isPresent()) {
                nextBookingDto = bookingMapper.mapToBookingDto(nextBooking.get());
            }
            ItemDto itemWithBooking = itemMapper.mapToItemDtoWithBookings(item, lastBookingDto,
                    nextBookingDto, commentsDto);
            log.info("Get item with bookings: " + itemWithBooking);
            return itemWithBooking;
        }
        log.info("Get item: " + item);
        return itemMapper.mapToItemDto(item, commentsDto);
    }

    @Override
    public List<Item> getByName(String name) {
        if (name.isBlank()) {
            log.info("Get list with empty items name.");
            return new ArrayList<>();
        }
        List<Item> items = itemStorage.findAllByName(name);
        log.info("Get items by name: " + items);
        return items;
    }

    @Override
    public List<ItemDto> getAllItemsByUserId(Long userId) {
        containsUser(userId);
        List<Item> items = itemStorage.findAllByOwnerId(userId);
        List<ItemDto> itemsDto = items.stream()
                                      .map(i -> getById(userId, i.getId()))
                                      .collect(Collectors.toList());
        log.info("Get items by user id: " + itemsDto);
        return itemsDto;
    }

    @Override
    public Item updateItem(Long userId, Long itemId, Item item) {
        containsUser(userId);
        Item itemFromStorage = containsItem(itemId);
        if (itemFromStorage.getOwnerId() != userId) {
            throw new NotFoundException(
                    "This item can update only user with id = " + itemFromStorage.getOwnerId());
        }
        if (item.getName() != null && !item.getName()
                                           .isBlank()) {
            itemFromStorage.setName(item.getName());
        }
        if (item.getDescription() != null && !item.getDescription()
                                                  .isBlank()) {
            itemFromStorage.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            itemFromStorage.setAvailable(item.getAvailable());
        }
        log.info("Item updated: " + itemFromStorage);
        return itemStorage.save(itemFromStorage);
    }

    @Override
    public void deleteItem(Long itemId) {
        Item item = containsItem(itemId);
        log.info("Deleted item with id: {}", itemId);
        itemStorage.delete(item);
    }

    @Override
    public CommentDto addCommentToItem(Long userId, Long itemId,
            CommentDtoRequest commentDtoRequest) {
        User user = containsUser(userId);
        Item item = containsItem(itemId);
        if (commentDtoRequest == null || commentDtoRequest.getText()
                                                          .isBlank()) {
            throw new ValidationException("Text of comment can't be empty.");
        }
        List<Booking> userBookingsByItemId = bookingStorage.findAllByBookerId(userId)
                                                           .stream()
                                                           .filter(booking -> booking.getItem()
                                                                                     .getId()
                                                                                     .equals(itemId))
                                                           .filter(booking -> booking.getStatus()
                                                                                     .equals(
                                                                                             BookingState.APPROVED))
                                                           .filter(booking -> booking.getEnd()
                                                                                     .isBefore(
                                                                                             LocalDateTime.now()))
                                                           .collect(Collectors.toList());
        if (userBookingsByItemId.size() == 0) {
            throw new ValidationException("This user can't add comment to this item.");
        }
        Comment comment = commentMapper.toComment(user, item, commentDtoRequest,
                LocalDateTime.now());
        log.info("Add comment: " + comment);
        return commentMapper.toCommentDto(commentRepository.save(comment));
    }

    private User containsUser(Long id) {
        return userStorage.findById(id)
                          .orElseThrow(() -> new NotFoundException(
                                  "User with id = " + id + " not exist."));
    }

    private Item containsItem(long id) {
        return itemStorage.findById(id)
                          .orElseThrow(() -> new NotFoundException("Item not found."));
    }

    private void validateItem(Item item) {
        if (item.getName() == null || item.getName()
                                          .isBlank()) {
            throw new ValidationException("You must specify the name.");
        }
        if (item.getDescription() == null || item.getDescription()
                                                 .isBlank()) {
            throw new ValidationException("You must specify the description.");
        }
        if (item.getAvailable() == null) {
            throw new ValidationException("You must specify the available.");
        }
    }
}