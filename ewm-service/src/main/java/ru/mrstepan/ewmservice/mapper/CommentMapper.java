package ru.mrstepan.ewmservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.mrstepan.ewmservice.dto.CommentResponseDto;
import ru.mrstepan.ewmservice.model.Comment;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CommentMapper {/*
    @Mapping(target = "authorId", expression = "java(comment.getAuthor().getId())")
    @Mapping(target = "eventId", expression = "java(comment.getEvent().getId())")
    CommentResponseDto toCommentResponseDto(Comment comment);

    List<CommentResponseDto> toCommentResponseDtoList(List<Comment> comments);*/
}
