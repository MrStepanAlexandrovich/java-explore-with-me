package ru.mrstepan.ewmservice.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private CommentMapper commentMapper;

    @InjectMocks
    private CommentServiceImpl commentService;

    private User user;
    private User anotherUser;
    private Event event;
    private Comment comment;
    private NewCommentDto newCommentDto;
    private CommentResponseDto commentResponseDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("test@test.com");

        anotherUser = new User();
        anotherUser.setId(2L);
        anotherUser.setName("Another User");
        anotherUser.setEmail("another@test.com");

        event = new Event();
        event.setId(1L);
        event.setTitle("Test Event");

        comment = new Comment();
        comment.setId(1);
        comment.setAuthor(user);
        comment.setEvent(event);
        comment.setText("Test comment");
        comment.setCreatedOn(LocalDateTime.now());
        comment.setEdited(false);

        newCommentDto = new NewCommentDto();
        newCommentDto.setText("Test comment");

        commentResponseDto = new CommentResponseDto();
        commentResponseDto.setId(1L);
        commentResponseDto.setAuthorId(1L);
        commentResponseDto.setEventId(1L);
        commentResponseDto.setText("Test comment");
        commentResponseDto.setEdited(false);
    }

    @Test
    void addComment_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);
        when(commentMapper.toCommentResponseDto(any(Comment.class))).thenReturn(commentResponseDto);

        CommentResponseDto result = commentService.addComment(1L, newCommentDto, 1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test comment", result.getText());
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    void addComment_UserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                commentService.addComment(999L, newCommentDto, 1L));

        verify(commentRepository, never()).save(any());
    }

    @Test
    void addComment_EventNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(eventRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                commentService.addComment(1L, newCommentDto, 999L));

        verify(commentRepository, never()).save(any());
    }

    @Test
    void deleteComment_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));

        assertDoesNotThrow(() -> commentService.deleteComment(1L, 1L, 1L));

        verify(commentRepository).deleteById(1L);
    }

    @Test
    void deleteComment_UserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                commentService.deleteComment(999L, 1L, 1L));

        verify(commentRepository, never()).deleteById(anyLong());
    }

    @Test
    void deleteComment_NotAuthor_ThrowsConflict() {
        Comment otherUserComment = new Comment();
        otherUserComment.setId(1);
        otherUserComment.setAuthor(anotherUser);
        otherUserComment.setEvent(event);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(commentRepository.findById(1L)).thenReturn(Optional.of(otherUserComment));

        assertThrows(ConflictException.class, () ->
                commentService.deleteComment(1L, 1L, 1L));

        verify(commentRepository, never()).deleteById(anyLong());
    }

    @Test
    void editComment_Success() {
        NewCommentDto editDto = new NewCommentDto();
        editDto.setText("Edited text");

        CommentResponseDto editedResponse = new CommentResponseDto();
        editedResponse.setId(1L);
        editedResponse.setText("Edited text");
        editedResponse.setEdited(true);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);
        when(commentMapper.toCommentResponseDto(any(Comment.class))).thenReturn(editedResponse);

        CommentResponseDto result = commentService.editComment(1L, 1L, 1L, editDto);

        assertNotNull(result);
        assertTrue(result.isEdited());
        assertEquals("Edited text", result.getText());
    }

    @Test
    void editComment_NotAuthor_ThrowsConflict() {
        Comment otherUserComment = new Comment();
        otherUserComment.setId(1);
        otherUserComment.setAuthor(anotherUser);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(commentRepository.findById(1L)).thenReturn(Optional.of(otherUserComment));

        assertThrows(ConflictException.class, () ->
                commentService.editComment(1L, 1L, 1L, newCommentDto));

        verify(commentRepository, never()).save(any());
    }

    @Test
    void getCommentsForEvent_Success() {
        List<Comment> comments = List.of(comment);
        List<CommentResponseDto> responseDtos = List.of(commentResponseDto);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(commentRepository.findByEventId(1L)).thenReturn(comments);
        when(commentMapper.toCommentResponseDtoList(comments)).thenReturn(responseDtos);

        Collection<CommentResponseDto> result = commentService.getCommentsForEvent(1L, 1L);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getCommentsForEvent_UserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                commentService.getCommentsForEvent(1L, 999L));
    }

    @Test
    void getCommentsForEvent_EventNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(eventRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                commentService.getCommentsForEvent(999L, 1L));
    }
}
