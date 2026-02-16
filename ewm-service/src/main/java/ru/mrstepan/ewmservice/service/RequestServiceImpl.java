package ru.mrstepan.ewmservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mrstepan.ewmservice.dao.EventRepository;
import ru.mrstepan.ewmservice.dao.RequestRepository;
import ru.mrstepan.ewmservice.dao.UserRepository;
import ru.mrstepan.ewmservice.dto.RequestDto;
import ru.mrstepan.ewmservice.exception.ConflictException;
import ru.mrstepan.ewmservice.exception.NotFoundException;
import ru.mrstepan.ewmservice.model.Event;
import ru.mrstepan.ewmservice.model.Request;
import ru.mrstepan.ewmservice.model.RequestMapper;
import ru.mrstepan.ewmservice.model.RequestStatus;
import ru.mrstepan.ewmservice.model.Status;
import ru.mrstepan.ewmservice.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    public List<RequestDto> getRequests(long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));
        return requestRepository.findAllByRequester_Id(userId).stream()
                .map(RequestMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public RequestDto addRequest(long userId, long eventId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));

        if (event.getState() != Status.PUBLISHED) {
            throw new ConflictException("Cannot participate in an unpublished event");
        }
        if (event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Event initiator cannot request participation in own event");
        }
        if (requestRepository.findByRequester_IdAndEvent_Id(userId, eventId).isPresent()) {
            throw new ConflictException("Duplicate request");
        }
        int limit = event.getParticipantLimit() != null ? event.getParticipantLimit() : 0;
        if (limit > 0) {
            long confirmed = requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
            if (confirmed >= limit) {
                throw new ConflictException("The participant limit has been reached");
            }
        }

        Request request = new Request();
        request.setRequester(user);
        request.setEvent(event);
        request.setCreated(LocalDateTime.now());

        boolean needsModeration = event.getRequestModeration() != null ? event.getRequestModeration() : true;
        if (!needsModeration) {
            request.setStatus(RequestStatus.CONFIRMED);
        } else {
            request.setStatus(RequestStatus.PENDING);
        }

        return RequestMapper.toDto(requestRepository.save(request));
    }

    @Override
    @Transactional
    public RequestDto cancelRequest(long userId, long requestId) {
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request with id=" + requestId + " was not found"));
        if (!request.getRequester().getId().equals(userId)) {
            throw new NotFoundException("Request with id=" + requestId + " was not found");
        }
        request.setStatus(RequestStatus.CANCELED);
        return RequestMapper.toDto(requestRepository.save(request));
    }
}
