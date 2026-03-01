package ru.mrstepan.ewmservice.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import ru.mrstepan.ewmservice.dao.CategoryRepository;
import ru.mrstepan.ewmservice.dao.EventRepository;
import ru.mrstepan.ewmservice.dao.RequestRepository;
import ru.mrstepan.ewmservice.dao.UserRepository;
import ru.mrstepan.ewmservice.dto.*;
import ru.mrstepan.ewmservice.exception.BadRequestException;
import ru.mrstepan.ewmservice.exception.ConflictException;
import ru.mrstepan.ewmservice.exception.NotFoundException;
import ru.mrstepan.ewmservice.model.*;
import ru.mrstepan.statsclient.StatsApiClientImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceImplTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RequestRepository requestRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private StatsApiClientImpl statsClient;

    @InjectMocks
    private EventServiceImpl eventService;

    private User user;
    private User anotherUser;
    private Category category;
    private Event event;
    private NewEventDto newEventDto;
    private Location location;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("test@test.com");

        anotherUser = new User();
        anotherUser.setId(2L);
        anotherUser.setName("Another User");

        category = new Category();
        category.setId(1L);
        category.setName("Test Category");

        location = new Location();
        location.setLat(55.75f);
        location.setLon(37.61f);

        event = new Event();
        event.setId(1L);
        event.setTitle("Test Event");
        event.setAnnotation("Test annotation");
        event.setDescription("Test description");
        event.setCategory(category);
        event.setInitiator(user);
        event.setState(Status.PENDING);
        event.setEventDate(LocalDateTime.now().plusDays(7));
        event.setLocation(location);
        event.setParticipantLimit(10);
        event.setRequestModeration(true);

        newEventDto = new NewEventDto();
        newEventDto.setTitle("Test Event");
        newEventDto.setAnnotation("Test annotation with enough characters");
        newEventDto.setDescription("Test description with enough characters");
        newEventDto.setCategory(1L);
        newEventDto.setEventDate(LocalDateTime.now().plusDays(7));
        newEventDto.setLocation(location);
    }

    @Test
    void getUserEvents_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(eventRepository.findAllByInitiator_Id(eq(1L), any(PageRequest.class)))
                .thenReturn(List.of(event));
        when(requestRepository.countByEventIdAndStatus(anyLong(), eq(RequestStatus.CONFIRMED)))
                .thenReturn(0L);

        List<EventShortDto> result = eventService.getUserEvents(1L, 0, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getUserEvents_UserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                eventService.getUserEvents(999L, 0, 10));
    }

    @Test
    void addEvent_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(eventRepository.save(any(Event.class))).thenReturn(event);

        EventFullDto result = eventService.addEvent(newEventDto, 1L);

        assertNotNull(result);
        verify(eventRepository).save(any(Event.class));
    }

    @Test
    void addEvent_UserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                eventService.addEvent(newEventDto, 999L));
    }

    @Test
    void addEvent_CategoryNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                eventService.addEvent(newEventDto, 1L));
    }

    @Test
    void addEvent_EventDateTooSoon_ThrowsBadRequest() {
        newEventDto.setEventDate(LocalDateTime.now().plusHours(1));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        assertThrows(BadRequestException.class, () ->
                eventService.addEvent(newEventDto, 1L));
    }

    @Test
    void getUserEvent_Success() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(requestRepository.countByEventIdAndStatus(1L, RequestStatus.CONFIRMED)).thenReturn(0L);

        EventFullDto result = eventService.getUserEvent(1L, 1L);

        assertNotNull(result);
        assertEquals("Test Event", result.getTitle());
    }

    @Test
    void getUserEvent_EventNotFound() {
        when(eventRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                eventService.getUserEvent(999L, 1L));
    }

    @Test
    void getUserEvent_NotOwner_ThrowsNotFound() {
        event.setInitiator(anotherUser);

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        assertThrows(NotFoundException.class, () ->
                eventService.getUserEvent(1L, 1L));
    }

    @Test
    void updateUserEvent_Success() {
        EventEditDto editDto = new EventEditDto();
        editDto.setTitle("Updated Title");

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(eventRepository.save(any(Event.class))).thenReturn(event);
        when(requestRepository.countByEventIdAndStatus(1L, RequestStatus.CONFIRMED)).thenReturn(0L);

        EventFullDto result = eventService.updateUserEvent(1L, 1L, editDto);

        assertNotNull(result);
    }

    @Test
    void updateUserEvent_PublishedEvent_ThrowsConflict() {
        event.setState(Status.PUBLISHED);
        EventEditDto editDto = new EventEditDto();

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        assertThrows(ConflictException.class, () ->
                eventService.updateUserEvent(1L, 1L, editDto));
    }

    @Test
    void updateUserEvent_EventDateTooSoon_ThrowsBadRequest() {
        EventEditDto editDto = new EventEditDto();
        editDto.setEventDate(LocalDateTime.now().plusMinutes(30));

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        assertThrows(BadRequestException.class, () ->
                eventService.updateUserEvent(1L, 1L, editDto));
    }

    @Test
    void updateUserEvent_SendToReview() {
        event.setState(Status.CANCELED);
        EventEditDto editDto = new EventEditDto();
        editDto.setStateAction("SEND_TO_REVIEW");

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(eventRepository.save(any(Event.class))).thenAnswer(inv -> {
            Event e = inv.getArgument(0);
            assertEquals(Status.PENDING, e.getState());
            return e;
        });
        when(requestRepository.countByEventIdAndStatus(1L, RequestStatus.CONFIRMED)).thenReturn(0L);

        eventService.updateUserEvent(1L, 1L, editDto);

        verify(eventRepository).save(any(Event.class));
    }

    @Test
    void updateUserEvent_CancelReview() {
        EventEditDto editDto = new EventEditDto();
        editDto.setStateAction("CANCEL_REVIEW");

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(eventRepository.save(any(Event.class))).thenAnswer(inv -> {
            Event e = inv.getArgument(0);
            assertEquals(Status.CANCELED, e.getState());
            return e;
        });
        when(requestRepository.countByEventIdAndStatus(1L, RequestStatus.CONFIRMED)).thenReturn(0L);

        eventService.updateUserEvent(1L, 1L, editDto);

        verify(eventRepository).save(any(Event.class));
    }

    @Test
    void getEventRequests_Success() {
        Request request = new Request();
        request.setId(1L);
        request.setRequester(anotherUser);
        request.setEvent(event);
        request.setStatus(RequestStatus.PENDING);

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(requestRepository.findAllByEvent_Id(1L)).thenReturn(List.of(request));

        List<RequestDto> result = eventService.getEventRequests(1L, 1L);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void changeRequestStatus_ConfirmRequests_Success() {
        Request request = new Request();
        request.setId(1L);
        request.setRequester(anotherUser);
        request.setEvent(event);
        request.setStatus(RequestStatus.PENDING);

        EventRequestStatusUpdateRequest updateRequest = new EventRequestStatusUpdateRequest();
        updateRequest.setRequestIds(List.of(1L));
        updateRequest.setStatus("CONFIRMED");

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(requestRepository.findAllByIdIn(List.of(1L))).thenReturn(List.of(request));
        when(requestRepository.countByEventIdAndStatus(1L, RequestStatus.CONFIRMED)).thenReturn(0L);
        when(requestRepository.save(any(Request.class))).thenAnswer(inv -> inv.getArgument(0));

        EventRequestStatusUpdateResult result = eventService.changeRequestStatus(1L, 1L, updateRequest);

        assertNotNull(result);
        assertEquals(1, result.getConfirmedRequests().size());
        assertEquals(0, result.getRejectedRequests().size());
    }

    @Test
    void changeRequestStatus_RejectRequests_Success() {
        Request request = new Request();
        request.setId(1L);
        request.setRequester(anotherUser);
        request.setEvent(event);
        request.setStatus(RequestStatus.PENDING);

        EventRequestStatusUpdateRequest updateRequest = new EventRequestStatusUpdateRequest();
        updateRequest.setRequestIds(List.of(1L));
        updateRequest.setStatus("REJECTED");

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(requestRepository.findAllByIdIn(List.of(1L))).thenReturn(List.of(request));
        when(requestRepository.save(any(Request.class))).thenAnswer(inv -> inv.getArgument(0));

        EventRequestStatusUpdateResult result = eventService.changeRequestStatus(1L, 1L, updateRequest);

        assertNotNull(result);
        assertEquals(0, result.getConfirmedRequests().size());
        assertEquals(1, result.getRejectedRequests().size());
    }

    @Test
    void changeRequestStatus_NotPending_ThrowsConflict() {
        Request request = new Request();
        request.setId(1L);
        request.setStatus(RequestStatus.CONFIRMED);

        EventRequestStatusUpdateRequest updateRequest = new EventRequestStatusUpdateRequest();
        updateRequest.setRequestIds(List.of(1L));
        updateRequest.setStatus("REJECTED");

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(requestRepository.findAllByIdIn(List.of(1L))).thenReturn(List.of(request));

        assertThrows(ConflictException.class, () ->
                eventService.changeRequestStatus(1L, 1L, updateRequest));
    }

    @Test
    void changeRequestStatus_LimitReached_ThrowsConflict() {
        event.setParticipantLimit(5);
        Request request = new Request();
        request.setId(1L);
        request.setStatus(RequestStatus.PENDING);

        EventRequestStatusUpdateRequest updateRequest = new EventRequestStatusUpdateRequest();
        updateRequest.setRequestIds(List.of(1L));
        updateRequest.setStatus("CONFIRMED");

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(requestRepository.findAllByIdIn(List.of(1L))).thenReturn(List.of(request));
        when(requestRepository.countByEventIdAndStatus(1L, RequestStatus.CONFIRMED)).thenReturn(5L);

        assertThrows(ConflictException.class, () ->
                eventService.changeRequestStatus(1L, 1L, updateRequest));
    }

    @Test
    @SuppressWarnings("unchecked")
    void getAdminEvents_Success() {
        Page<Event> eventPage = new PageImpl<>(List.of(event));

        when(eventRepository.findAll(any(Specification.class), any(PageRequest.class)))
                .thenReturn(eventPage);
        when(requestRepository.countByEventIdAndStatus(anyLong(), eq(RequestStatus.CONFIRMED)))
                .thenReturn(0L);

        List<EventFullDto> result = eventService.getAdminEvents(
                null, null, null, null, null, 0, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void updateAdminEvent_PublishEvent_Success() {
        EventEditDto editDto = new EventEditDto();
        editDto.setStateAction("PUBLISH_EVENT");

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(eventRepository.save(any(Event.class))).thenAnswer(inv -> {
            Event e = inv.getArgument(0);
            assertEquals(Status.PUBLISHED, e.getState());
            return e;
        });
        when(requestRepository.countByEventIdAndStatus(1L, RequestStatus.CONFIRMED)).thenReturn(0L);

        EventFullDto result = eventService.updateAdminEvent(1L, editDto);

        assertNotNull(result);
    }

    @Test
    void updateAdminEvent_PublishNotPending_ThrowsConflict() {
        event.setState(Status.CANCELED);
        EventEditDto editDto = new EventEditDto();
        editDto.setStateAction("PUBLISH_EVENT");

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        assertThrows(ConflictException.class, () ->
                eventService.updateAdminEvent(1L, editDto));
    }

    @Test
    void updateAdminEvent_RejectEvent_Success() {
        EventEditDto editDto = new EventEditDto();
        editDto.setStateAction("REJECT_EVENT");

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(eventRepository.save(any(Event.class))).thenAnswer(inv -> {
            Event e = inv.getArgument(0);
            assertEquals(Status.CANCELED, e.getState());
            return e;
        });
        when(requestRepository.countByEventIdAndStatus(1L, RequestStatus.CONFIRMED)).thenReturn(0L);

        EventFullDto result = eventService.updateAdminEvent(1L, editDto);

        assertNotNull(result);
    }

    @Test
    void updateAdminEvent_RejectPublished_ThrowsConflict() {
        event.setState(Status.PUBLISHED);
        EventEditDto editDto = new EventEditDto();
        editDto.setStateAction("REJECT_EVENT");

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        assertThrows(ConflictException.class, () ->
                eventService.updateAdminEvent(1L, editDto));
    }

    @Test
    @SuppressWarnings("unchecked")
    void getPublicEvents_Success() {
        event.setState(Status.PUBLISHED);
        Page<Event> eventPage = new PageImpl<>(List.of(event));

        when(eventRepository.findAll(any(Specification.class), any(PageRequest.class)))
                .thenReturn(eventPage);
        when(requestRepository.countByEventIdAndStatus(anyLong(), eq(RequestStatus.CONFIRMED)))
                .thenReturn(0L);

        List<EventShortDto> result = eventService.getPublicEvents(
                null, null, null, null, null, false, null, 0, 10);

        assertNotNull(result);
    }

    @Test
    void getPublicEvents_InvalidDateRange_ThrowsBadRequest() {
        LocalDateTime start = LocalDateTime.now().plusDays(5);
        LocalDateTime end = LocalDateTime.now().plusDays(1);

        assertThrows(BadRequestException.class, () ->
                eventService.getPublicEvents(null, null, null, start, end, false, null, 0, 10));
    }

    @Test
    void getPublicEvent_Success() {
        event.setState(Status.PUBLISHED);

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(requestRepository.countByEventIdAndStatus(1L, RequestStatus.CONFIRMED)).thenReturn(0L);
        when(statsClient.getStats(any(), any(), any(), anyBoolean())).thenReturn(List.of());

        EventFullDto result = eventService.getPublicEvent(1L, "/events/1");

        assertNotNull(result);
    }

    @Test
    void getPublicEvent_NotPublished_ThrowsNotFound() {
        event.setState(Status.PENDING);

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        assertThrows(NotFoundException.class, () ->
                eventService.getPublicEvent(1L, "/events/1"));
    }

    @Test
    void getPublicEvent_EventNotFound() {
        when(eventRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                eventService.getPublicEvent(999L, "/events/999"));
    }
}
