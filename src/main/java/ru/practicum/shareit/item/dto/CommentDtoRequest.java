package ru.practicum.shareit.item.dto;

import lombok.Data;

@Data
public class CommentDtoRequest {

    private long id;
    private String text;
}