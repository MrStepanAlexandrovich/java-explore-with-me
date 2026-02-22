package ru.mrstepan.ewmservice.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentResponseDto {
    private long id;
    private long authorId;
    private long eventId;
    private LocalDateTime createdOn;
    private String text;
    private boolean edited;
}
