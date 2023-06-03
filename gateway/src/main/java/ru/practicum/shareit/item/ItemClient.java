package ru.practicum.shareit.item;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDtoRequest;
import ru.practicum.shareit.item.dto.ItemDto;

@Service
public class ItemClient extends BaseClient {

    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> createItem(long userId, ItemDto requestDto) {
        if (requestDto.getName() == null || requestDto.getName().isBlank()) {
            throw new ValidationException("You must specify the name.");
        }
        if (requestDto.getDescription() == null || requestDto.getDescription().isBlank()) {
            throw new ValidationException("You must specify the description.");
        }
        if (requestDto.getAvailable() == null) {
            throw new ValidationException("You must specify the available.");
        }
        return post("", userId, requestDto);
    }

    public ResponseEntity<Object> getItem(long userId, long itemId) {
        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> getItemsByName(String text, long userId, int from, int size) {
        Map<String, Object> parameters = Map.of(
                "text", text,
                "from", from,
                "size", size
        );
        return get("/search?text={text}&from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> getAllItemsByUserId(long userId, int from, int size) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        return get("?from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> updateItem(long userId, long itemId, ItemDto requestDto) {
        return patch("/" + itemId, userId, requestDto);
    }

    public ResponseEntity<Object> deleteItem(long itemId) {
        return delete("/" + itemId);
    }

    public ResponseEntity<Object> addComment(long userId, long itemId, CommentDtoRequest requestDto) {
        return post("/" + itemId + "/comment", userId, requestDto);
    }
}