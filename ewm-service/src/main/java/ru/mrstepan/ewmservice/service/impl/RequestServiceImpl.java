package ru.mrstepan.ewmservice.service.impl;

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
import ru.mrstepan.ewmservice.mapper.RequestMapper;
import ru.mrstepan.ewmservice.model.RequestStatus;
import ru.mrstepan.ewmservice.model.Status;
import ru.mrstepan.ewmservice.model.User;
import ru.mrstepan.ewmservice.service.RequestService;

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
        log.info("Getting requests for user with id: {}", userId);
        userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User with id={} was not found", userId);
                    return new NotFoundException("User with id=" + userId + " was not found");
                });
        List<RequestDto> requests = requestRepository.findAllByRequester_Id(userId).stream()
                .map(RequestMapper::toDto)
                .collect(Collectors.toList());
        log.info("Found {} requests for user with id: {}", requests.size(), userId);
        return requests;
    }

    @Override
    @Transactional
    public RequestDto addRequest(long userId, long eventId) {
        log.info("Adding request. userId: {}, eventId: {}", userId, eventId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User with id={} was not found", userId);
                    return new NotFoundException("User with id=" + userId + " was not found");
                });
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> {
                    log.error("Event with id={} was not found", eventId);
                    return new NotFoundException("Event with id=" + eventId + " was not found");
                });

        if (event.getState() != Status.PUBLISHED) {
            log.error("Cannot participate in an unpublished event. eventId: {}", eventId);
            throw new ConflictException("Cannot participate in an unpublished event");
        }
        if (event.getInitiator().getId().equals(userId)) {
            log.error("Event initiator cannot request participation in own event. userId: {}, eventId: {}", userId, eventId);
            throw new ConflictException("Event initiator cannot request participation in own event");
        }
        if (requestRepository.findByRequester_IdAndEvent_Id(userId, eventId).isPresent()) {
            log.error("Duplicate request. userId: {}, eventId: {}", userId, eventId);
            throw new ConflictException("Duplicate request");
        }
        int limit = event.getParticipantLimit() != null ? event.getParticipantLimit() : 0;
        if (limit > 0) {
            long confirmed = requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
            if (confirmed >= limit) {
                log.error("The participant limit has been reached. eventId: {}", eventId);
                throw new ConflictException("The participant limit has been reached");
            }
        }

        Request request = new Request();
        request.setRequester(user);
        request.setEvent(event);
        request.setCreated(LocalDateTime.now());

        boolean needsModeration = event.getRequestModeration() != null ? event.getRequestModeration() : true;
        int participantLimit = event.getParticipantLimit() != null ? event.getParticipantLimit() : 0;

        if (!needsModeration || participantLimit == 0) {
            request.setStatus(RequestStatus.CONFIRMED);
            log.info("Request auto-confirmed (no moderation required)");
        } else {
            request.setStatus(RequestStatus.PENDING);
            log.info("Request set to PENDING (moderation required)");
        }

        Request saved = requestRepository.save(request);
        log.info("Request saved. Id: {}, status: {}", saved.getId(), saved.getStatus());
        return RequestMapper.toDto(saved);
    }

    @Override
    @Transactional
    public RequestDto cancelRequest(long userId, long requestId) {
        log.info("Cancelling request. userId: {}, requestId: {}", userId, requestId);
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> {
                    log.error("Request with id={} was not found", requestId);
                    return new NotFoundException("Request with id=" + requestId + " was not found");
                });
        if (!request.getRequester().getId().equals(userId)) {
            log.error("Request with id={} was not found for user with id={}", requestId, userId);
            throw new NotFoundException("Request with id=" + requestId + " was not found");
        }
        request.setStatus(RequestStatus.CANCELED);
        Request saved = requestRepository.save(request);
        log.info("Request with id: {} cancelled", saved.getId());
        return RequestMapper.toDto(saved);
    }
}
