package ru.practicum.shareit.request;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.request.dto.ItemRequestDto;

@Component
public class ItemRequestMapper {

    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        if (itemRequest.getRequester() == null || itemRequest.getDescription() == null || itemRequest.getCreated() == null) {
            throw new ValidationException("All itemRequest fields must be filled in.");
        }
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(itemRequest.getId());
        itemRequestDto.setDescription(itemRequest.getDescription());
        itemRequestDto.setRequester(itemRequest.getRequester());
        itemRequestDto.setCreated(itemRequest.getCreated());
        return itemRequestDto;
    }

    public static ItemRequest toItemRequest(ItemRequestDto itemRequestDto) {
        if (itemRequestDto.getRequester() == null || itemRequestDto.getDescription() == null || itemRequestDto.getCreated() == null) {
            throw new ValidationException("All itemRequestDto fields must be filled in.");
        }
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(itemRequestDto.getId());
        itemRequest.setDescription(itemRequestDto.getDescription());
        itemRequest.setRequester(itemRequestDto.getRequester());
        itemRequest.setCreated(itemRequestDto.getCreated());
        return itemRequest;
    }
}