package ru.mrstepan.ewmservice.service;

import ru.mrstepan.ewmservice.dto.CommentResponseDto;
import ru.mrstepan.ewmservice.dto.NewCommentDto;

import java.util.Collection;

public interface CommentService {
    CommentResponseDto addComment(long userId, NewCommentDto commentDto, long eventId);

    void deleteComment(long userId, long eventId, long commentId);

    CommentResponseDto editComment(long userId, long eventId, long commentId, NewCommentDto commentDto);

    Collection<CommentResponseDto> getCommentsForEvent(long eventId, long userId);
}
