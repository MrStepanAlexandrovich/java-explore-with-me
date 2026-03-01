package ru.mrstepan.ewmservice.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.mrstepan.ewmservice.dao.EventRepository;
import ru.mrstepan.ewmservice.dao.RequestRepository;
import ru.mrstepan.ewmservice.dao.UserRepository;
import ru.mrstepan.ewmservice.dto.RequestDto;
import ru.mrstepan.ewmservice.exception.ConflictException;
import ru.mrstepan.ewmservice.exception.NotFoundException;
import ru.mrstepan.ewmservice.model.Event;
import ru.mrstepan.ewmservice.model.Request;
import ru.mrstepan.ewmservice.model.RequestStatus;
import ru.mrstepan.ewmservice.model.Status;
import ru.mrstepan.ewmservice.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RequestServiceImplTest {

    @Mock
    private RequestRepository requestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private RequestServiceImpl requestService;

    private User user;
    private User eventOwner;
    private Event event;
    private Request request;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("test@test.com");

        eventOwner = new User();
        eventOwner.setId(2L);
        eventOwner.setName("Event Owner");
        eventOwner.setEmail("owner@test.com");

        event = new Event();
        event.setId(1L);
        event.setTitle("Test Event");
        event.setInitiator(eventOwner);
        event.setState(Status.PUBLISHED);
        event.setParticipantLimit(10);
        event.setRequestModeration(true);

        request = new Request();
        request.setId(1L);
        request.setRequester(user);
        request.setEvent(event);
        request.setStatus(RequestStatus.PENDING);
        request.setCreated(LocalDateTime.now());
    }

    @Test
    void getRequests_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(requestRepository.findAllByRequester_Id(1L)).thenReturn(List.of(request));

        List<RequestDto> result = requestService.getRequests(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getRequests_UserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                requestService.getRequests(999L));
    }

    @Test
    void addRequest_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(requestRepository.findByRequester_IdAndEvent_Id(1L, 1L)).thenReturn(Optional.empty());
        when(requestRepository.countByEventIdAndStatus(1L, RequestStatus.CONFIRMED)).thenReturn(0L);
        when(requestRepository.save(any(Request.class))).thenReturn(request);

        RequestDto result = requestService.addRequest(1L, 1L);

        assertNotNull(result);
        verify(requestRepository).save(any(Request.class));
    }

    @Test
    void addRequest_UserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                requestService.addRequest(999L, 1L));
    }

    @Test
    void addRequest_EventNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(eventRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                requestService.addRequest(1L, 999L));
    }

    @Test
    void addRequest_EventNotPublished_ThrowsConflict() {
        event.setState(Status.PENDING);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        assertThrows(ConflictException.class, () ->
                requestService.addRequest(1L, 1L));
    }

    @Test
    void addRequest_OwnEvent_ThrowsConflict() {
        event.setInitiator(user);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        assertThrows(ConflictException.class, () ->
                requestService.addRequest(1L, 1L));
    }

    @Test
    void addRequest_DuplicateRequest_ThrowsConflict() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(requestRepository.findByRequester_IdAndEvent_Id(1L, 1L)).thenReturn(Optional.of(request));

        assertThrows(ConflictException.class, () ->
                requestService.addRequest(1L, 1L));
    }

    @Test
    void addRequest_LimitReached_ThrowsConflict() {
        event.setParticipantLimit(5);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(requestRepository.findByRequester_IdAndEvent_Id(1L, 1L)).thenReturn(Optional.empty());
        when(requestRepository.countByEventIdAndStatus(1L, RequestStatus.CONFIRMED)).thenReturn(5L);

        assertThrows(ConflictException.class, () ->
                requestService.addRequest(1L, 1L));
    }

    @Test
    void addRequest_NoModeration_AutoConfirmed() {
        event.setRequestModeration(false);

        Request confirmedRequest = new Request();
        confirmedRequest.setId(1L);
        confirmedRequest.setRequester(user);
        confirmedRequest.setEvent(event);
        confirmedRequest.setStatus(RequestStatus.CONFIRMED);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(requestRepository.findByRequester_IdAndEvent_Id(1L, 1L)).thenReturn(Optional.empty());
        when(requestRepository.countByEventIdAndStatus(1L, RequestStatus.CONFIRMED)).thenReturn(0L);
        when(requestRepository.save(any(Request.class))).thenReturn(confirmedRequest);

        RequestDto result = requestService.addRequest(1L, 1L);

        assertNotNull(result);
        assertEquals(RequestStatus.CONFIRMED.name(), result.getStatus());
    }

    @Test
    void cancelRequest_Success() {
        when(requestRepository.findById(1L)).thenReturn(Optional.of(request));
        when(requestRepository.save(any(Request.class))).thenAnswer(inv -> {
            Request r = inv.getArgument(0);
            r.setStatus(RequestStatus.CANCELED);
            return r;
        });

        RequestDto result = requestService.cancelRequest(1L, 1L);

        assertNotNull(result);
        assertEquals(RequestStatus.CANCELED.name(), result.getStatus());
    }

    @Test
    void cancelRequest_RequestNotFound() {
        when(requestRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                requestService.cancelRequest(1L, 999L));
    }

    @Test
    void cancelRequest_NotOwner_ThrowsNotFound() {
        User anotherUser = new User();
        anotherUser.setId(3L);
        request.setRequester(anotherUser);

        when(requestRepository.findById(1L)).thenReturn(Optional.of(request));

        assertThrows(NotFoundException.class, () ->
                requestService.cancelRequest(1L, 1L));
    }
}
