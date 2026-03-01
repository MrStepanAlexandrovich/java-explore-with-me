package ru.mrstepan.ewmservice.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.mrstepan.ewmservice.dao.CommentRepository;
import ru.mrstepan.ewmservice.dao.EventRepository;
import ru.mrstepan.ewmservice.dao.UserRepository;
import ru.mrstepan.ewmservice.dto.CommentResponseDto;
import ru.mrstepan.ewmservice.dto.NewCommentDto;
import ru.mrstepan.ewmservice.exception.ConflictException;
import ru.mrstepan.ewmservice.exception.NotFoundException;
import ru.mrstepan.ewmservice.mapper.CommentMapper;
import ru.mrstepan.ewmservice.model.Comment;
import ru.mrstepan.ewmservice.model.Event;
import ru.mrstepan.ewmservice.model.User;
import ru.mrstepan.ewmservice.service.CommentService;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final CommentMapper commentMapper;

    @Override
    public CommentResponseDto addComment(long userId, NewCommentDto commentDto, long eventId) {
        log.info("Adding comment text: {}, authorId: {}, eventId: {}", commentDto.getText(), userId, eventId);
        Comment comment = new Comment();

        comment.setCreatedOn(LocalDateTime.now());
        comment.setText(commentDto.getText());
        log.trace("Getting user id: {}", userId);

        User user = userRepository.findById(userId).orElseThrow(() -> {
            log.error("User id: {} wasn't found", userId);
            return new NotFoundException("User id: " + userId + " wasn't found");
        });

        log.trace("User was found id: {}", userId);

        comment.setAuthor(user);
        log.trace("Getting event id: {}", eventId);

        Event event = eventRepository.findById(eventId).orElseThrow(() -> {
            log.error("Event id: {} wasn't found", eventId);
            return new NotFoundException("Event id: " + eventId + " wasn't found");
        });

        log.trace("Event was found id: {}", eventId);

        comment.setEvent(event);

        Comment savedComment = commentRepository.save(comment);

        log.info("Comment was saved. Id: {}, text: {}, authorId: {}, eventId:{}, createdOn: {}, edited: {}",
                comment.getId(), comment.getText(), comment.getAuthor().getId(), comment.getEvent().getId(),
                comment.getCreatedOn(), comment.isEdited());

        return commentMapper.toCommentResponseDto(savedComment);
    }

    @Override
    public void deleteComment(long userId, long eventId, long commentId) {
        log.info("Deleting comment id: {}, eventId: {}, userId: {}", commentId, eventId, userId);

        log.trace("Getting user id: {}", userId);

        userRepository.findById(userId).orElseThrow(() -> {
            log.error("User id: {} wasn't found", userId);
            return new NotFoundException("User id: " + userId + " wasn't found");
        });

        log.trace("""
                User was found id: {}
                Getting event id: {}
                """, userId, eventId);

        eventRepository.findById(eventId).orElseThrow(() -> {
            log.error("Event id: {} wasn't found", eventId);
            return new NotFoundException("Event id: " + eventId + " wasn't found");
        });

        log.trace("Event was found id: {}", eventId);

        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> {
                    log.error("Event id: {} wasn't found", eventId);
                    return new NotFoundException("Event id: " + eventId + " wasn't found");
                }
        );

        if (comment.getAuthor().getId() != userId) {
            log.error("User id: {} cannot delete comment id: {}", userId, commentId);
            throw new ConflictException("User id: " + userId + " cannot delete comment id: " + commentId);
        } else {
            commentRepository.deleteById(commentId);
            log.info("Comment id: {} was deleted", commentId);
        }
    }

    @Override
    public CommentResponseDto editComment(long userId, long eventId, long commentId, NewCommentDto commentDto) {
        log.info("Editing comment id: {}, eventId: {}, userId: {}", commentId, eventId, userId);

        log.trace("Getting user id: {}", userId);

        userRepository.findById(userId).orElseThrow(() -> {
            log.error("User id: {} wasn't found", userId);
            return new NotFoundException("User id: " + userId + " wasn't found");
        });

        log.trace("""
                User was found id: {}
                Getting event id: {}
                """, userId, eventId);

        eventRepository.findById(eventId).orElseThrow(() -> {
            log.error("Event id: {} wasn't found", eventId);
            return new NotFoundException("Event id: " + eventId + " wasn't found");
        });

        log.trace("Event was found id: {}", eventId);

        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> {
                    log.error("Event id: {} wasn't found", eventId);
                    return new NotFoundException("Event id: " + eventId + " wasn't found");
                }
        );

        if (comment.getAuthor().getId() != userId) {
            log.error("User id: {} cannot delete comment id: {}", userId, commentId);
            throw new ConflictException("User id: " + userId + " cannot delete comment id: " + commentId);
        } else {
            comment.setEdited(true);
            comment.setText(commentDto.getText());
            comment.setCreatedOn(LocalDateTime.now());
            commentRepository.save(comment);
            log.info("Comment id: {} was edited. Text: {}, authorId: {}, eventId: {}, createdOn: {}, edited: {}",
                    commentId, comment.getText(), comment.getAuthor().getId(), comment.getEvent().getId(),
                    comment.getCreatedOn(), comment.isEdited());
        }

        return commentMapper.toCommentResponseDto(comment);
    }

    @Override
    public Collection<CommentResponseDto> getCommentsForEvent(long eventId, long userId) {
        log.info("User id: {} is getting comments for event id: {}", userId, eventId);

        log.trace("Getting user id: {}", userId);

        userRepository.findById(userId).orElseThrow(() -> {
            log.error("User id: {} wasn't found", userId);
            return new NotFoundException("User id: " + userId + " wasn't found");
        });

        log.trace("""
                User was found id: {}
                Getting event id: {}
                """, userId, eventId);

        eventRepository.findById(eventId).orElseThrow(() -> {
            log.error("Event id: {} wasn't found", eventId);
            return new NotFoundException("Event id: " + eventId + " wasn't found");
        });

        log.trace("Event was found id: {}", eventId);

        List<Comment> comments = commentRepository.findByEventId(eventId);

        log.info("For event id: {} found {} comments", eventId, comments.size());

        return commentMapper.toCommentResponseDtoList(comments);
    }
}
