package ru.mrstepan.ewmservice.service.impl;

import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mrstepan.ewmservice.dao.CategoryRepository;
import ru.mrstepan.ewmservice.dao.EventRepository;
import ru.mrstepan.ewmservice.dao.RequestRepository;
import ru.mrstepan.ewmservice.dao.UserRepository;
import ru.mrstepan.ewmservice.dto.EventEditDto;
import ru.mrstepan.ewmservice.dto.EventFullDto;
import ru.mrstepan.ewmservice.dto.EventRequestStatusUpdateRequest;
import ru.mrstepan.ewmservice.dto.EventRequestStatusUpdateResult;
import ru.mrstepan.ewmservice.dto.EventShortDto;
import ru.mrstepan.ewmservice.dto.NewEventDto;
import ru.mrstepan.ewmservice.dto.RequestDto;
import ru.mrstepan.ewmservice.exception.BadRequestException;
import ru.mrstepan.ewmservice.exception.ConflictException;
import ru.mrstepan.ewmservice.exception.NotFoundException;
import ru.mrstepan.ewmservice.model.Category;
import ru.mrstepan.ewmservice.model.Event;
import ru.mrstepan.ewmservice.mapper.EventMapper;
import ru.mrstepan.ewmservice.model.Request;
import ru.mrstepan.ewmservice.mapper.RequestMapper;
import ru.mrstepan.ewmservice.model.RequestStatus;
import ru.mrstepan.ewmservice.model.Status;
import ru.mrstepan.ewmservice.model.User;
import ru.mrstepan.ewmservice.service.EventService;
import ru.mrstepan.statsclient.StatsApiClientImpl;
import ru.mrstepan.statsdto.EndpointStatDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final RequestRepository requestRepository;
    private final CategoryRepository categoryRepository;
    private final StatsApiClientImpl statsClient;

    @Override
    public List<EventShortDto> getUserEvents(long userId, int from, int size) {
        log.info("Getting events for user with id: {}, from: {}, size: {}", userId, from, size);
        userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User with id={} was not found", userId);
                    return new NotFoundException("User with id=" + userId + " was not found");
                });
        List<EventShortDto> events = eventRepository.findAllByInitiator_Id(userId, PageRequest.of(from / size, size)).stream()
                .map(e -> EventMapper.toShortDto(e, getConfirmed(e.getId()), 0))
                .collect(Collectors.toList());
        log.info("Found {} events for user with id: {}", events.size(), userId);
        return events;
    }

    @Override
    public EventFullDto addEvent(NewEventDto dto, long userId) {
        log.info("Adding event. Title: {}, userId: {}", dto.getTitle(), userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User with id={} was not found", userId);
                    return new NotFoundException("User with id=" + userId + " was not found");
                });
        Category category = categoryRepository.findById(dto.getCategory())
                .orElseThrow(() -> {
                    log.error("Category with id={} was not found", dto.getCategory());
                    return new NotFoundException("Category with id=" + dto.getCategory() + " was not found");
                });

        if (dto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            log.error("Event date must be at least 2 hours from now. Value: {}", dto.getEventDate());
            throw new BadRequestException("Field: eventDate. Error: должно содержать дату, которая еще не наступила. Value: " + dto.getEventDate());
        }

        Event event = EventMapper.toEvent(dto, category, user);
        Event saved = eventRepository.save(event);
        log.info("Event saved. Id: {}", saved.getId());
        return EventMapper.toFullDto(saved, 0, 0);
    }

    @Override
    public EventFullDto getUserEvent(long eventId, long userId) {
        log.info("Getting event with id: {} for user with id: {}", eventId, userId);
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> {
                    log.error("Event with id={} was not found", eventId);
                    return new NotFoundException("Event with id=" + eventId + " was not found");
                });
        if (!event.getInitiator().getId().equals(userId)) {
            log.error("Event with id={} was not found for user with id={}", eventId, userId);
            throw new NotFoundException("Event with id=" + eventId + " was not found");
        }
        log.info("Found event: {}", event.getTitle());
        return EventMapper.toFullDto(event, getConfirmed(eventId), 0);
    }

    @Override
    public EventFullDto updateUserEvent(long userId, long eventId, EventEditDto dto) {
        log.info("Updating event with id: {} by user with id: {}", eventId, userId);
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> {
                    log.error("Event with id={} was not found", eventId);
                    return new NotFoundException("Event with id=" + eventId + " was not found");
                });
        if (!event.getInitiator().getId().equals(userId)) {
            log.error("Event with id={} was not found for user with id={}", eventId, userId);
            throw new NotFoundException("Event with id=" + eventId + " was not found");
        }
        if (event.getState() == Status.PUBLISHED) {
            log.error("Only pending or canceled events can be changed. eventId: {}, state: {}", eventId, event.getState());
            throw new ConflictException("Only pending or canceled events can be changed");
        }
        if (dto.getEventDate() != null) {
            if (dto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
                log.error("Event date must be at least 2 hours from now. Value: {}", dto.getEventDate());
                throw new BadRequestException("Field: eventDate. Error: must be in future. Value: " + dto.getEventDate());
            }
            event.setEventDate(dto.getEventDate());
        }
        applyEventEdit(event, dto);
        if ("SEND_TO_REVIEW".equals(dto.getStateAction())) {
            log.info("Event state changed to PENDING");
            event.setState(Status.PENDING);
        } else if ("CANCEL_REVIEW".equals(dto.getStateAction())) {
            log.info("Event state changed to CANCELED");
            event.setState(Status.CANCELED);
        }
        Event saved = eventRepository.save(event);
        log.info("Event with id: {} updated", saved.getId());
        return EventMapper.toFullDto(saved, getConfirmed(eventId), 0);
    }

    @Override
    public List<RequestDto> getEventRequests(long userId, long eventId) {
        log.info("Getting requests for event with id: {} by user with id: {}", eventId, userId);
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> {
                    log.error("Event with id={} was not found", eventId);
                    return new NotFoundException("Event with id=" + eventId + " was not found");
                });
        if (!event.getInitiator().getId().equals(userId)) {
            log.error("Event with id={} was not found for user with id={}", eventId, userId);
            throw new NotFoundException("Event with id=" + eventId + " was not found");
        }
        List<RequestDto> requests = requestRepository.findAllByEvent_Id(eventId).stream()
                .map(RequestMapper::toDto)
                .collect(Collectors.toList());
        log.info("Found {} requests for event with id: {}", requests.size(), eventId);
        return requests;
    }

    @Override
    public EventRequestStatusUpdateResult changeRequestStatus(long userId, long eventId,
                                                              EventRequestStatusUpdateRequest request) {
        log.info("Changing request status for event with id: {}. New status: {}, request ids: {}",
                eventId, request.getStatus(), request.getRequestIds());
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> {
                    log.error("Event with id={} was not found", eventId);
                    return new NotFoundException("Event with id=" + eventId + " was not found");
                });
        if (!event.getInitiator().getId().equals(userId)) {
            log.error("Event with id={} was not found for user with id={}", eventId, userId);
            throw new NotFoundException("Event with id=" + eventId + " was not found");
        }

        List<Request> requests = requestRepository.findAllByIdIn(request.getRequestIds());
        RequestStatus newStatus = RequestStatus.valueOf(request.getStatus());

        for (Request req : requests) {
            if (req.getStatus() != RequestStatus.PENDING) {
                log.error("Request with id={} must have status PENDING, but has {}", req.getId(), req.getStatus());
                throw new ConflictException("Request must have status PENDING");
            }
        }

        if (newStatus == RequestStatus.CONFIRMED) {
            int limit = event.getParticipantLimit() != null ? event.getParticipantLimit() : 0;
            long confirmed = getConfirmed(eventId);
            if (limit > 0 && confirmed >= limit) {
                log.error("The participant limit has been reached for event with id={}", eventId);
                throw new ConflictException("The participant limit has been reached");
            }
            List<RequestDto> confirmedList = new ArrayList<>();
            List<RequestDto> rejectedList = new ArrayList<>();
            for (Request req : requests) {
                if (limit == 0 || confirmed < limit) {
                    req.setStatus(RequestStatus.CONFIRMED);
                    confirmedList.add(RequestMapper.toDto(requestRepository.save(req)));
                    log.info("Request with id: {} confirmed", req.getId());
                    confirmed++;
                } else {
                    req.setStatus(RequestStatus.REJECTED);
                    rejectedList.add(RequestMapper.toDto(requestRepository.save(req)));
                    log.info("Request with id: {} rejected (limit reached)", req.getId());
                }
            }
            log.info("Request status update completed. Confirmed: {}, rejected: {}", confirmedList.size(), rejectedList.size());
            return new EventRequestStatusUpdateResult(confirmedList, rejectedList);
        } else {
            List<RequestDto> rejectedList = requests.stream()
                    .peek(r -> {
                        r.setStatus(RequestStatus.REJECTED);
                        log.info("Request with id: {} rejected", r.getId());
                    })
                    .map(r -> RequestMapper.toDto(requestRepository.save(r)))
                    .collect(Collectors.toList());
            log.info("Request status update completed. Rejected: {}", rejectedList.size());
            return new EventRequestStatusUpdateResult(List.of(), rejectedList);
        }
    }

    @Override
    public List<EventFullDto> getAdminEvents(List<Long> users, List<String> states, List<Long> categories,
                                             LocalDateTime rangeStart, LocalDateTime rangeEnd, int from, int size) {
        log.info("Admin getting events. users: {}, states: {}, categories: {}, rangeStart: {}, rangeEnd: {}, from: {}, size: {}",
                users, states, categories, rangeStart, rangeEnd, from, size);
        Specification<Event> spec = buildAdminSpec(users, states, categories, rangeStart, rangeEnd);
        List<EventFullDto> events = eventRepository.findAll(spec, PageRequest.of(from / size, size)).stream()
                .map(e -> EventMapper.toFullDto(e, getConfirmed(e.getId()), 0))
                .collect(Collectors.toList());
        log.info("Admin found {} events", events.size());
        return events;
    }

    @Override
    public EventFullDto updateAdminEvent(long eventId, EventEditDto dto) {
        log.info("Admin updating event with id: {}", eventId);
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> {
                    log.error("Event with id={} was not found", eventId);
                    return new NotFoundException("Event with id=" + eventId + " was not found");
                });

        if ("PUBLISH_EVENT".equals(dto.getStateAction())) {
            if (event.getState() != Status.PENDING) {
                log.error("Cannot publish event with id={} because it's not in the right state: {}", eventId, event.getState());
                throw new ConflictException("Cannot publish the event because it's not in the right state: " + event.getState());
            }
            LocalDateTime checkDate = dto.getEventDate() != null
                    ? dto.getEventDate() : event.getEventDate();
            if (checkDate.isBefore(LocalDateTime.now().plusHours(1))) {
                log.error("Cannot publish event with id={}: event date must be at least 1 hour from now", eventId);
                throw new ConflictException("Cannot publish: event date must be at least 1 hour from now");
            }
            event.setState(Status.PUBLISHED);
            event.setPublishedOn(LocalDateTime.now());
            log.info("Event with id: {} published", eventId);
        } else if ("REJECT_EVENT".equals(dto.getStateAction())) {
            if (event.getState() == Status.PUBLISHED) {
                log.error("Cannot reject published event with id={}", eventId);
                throw new ConflictException("Cannot reject published event");
            }
            event.setState(Status.CANCELED);
            log.info("Event with id: {} rejected", eventId);
        }

        if (dto.getEventDate() != null) {
            event.setEventDate(dto.getEventDate());
        }

        applyEventEdit(event, dto);
        Event saved = eventRepository.save(event);
        log.info("Admin updated event with id: {}", saved.getId());
        return EventMapper.toFullDto(saved, getConfirmed(eventId), 0);
    }

    @Override
    public List<EventShortDto> getPublicEvents(String text, List<Long> categories, Boolean paid,
                                               LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                               boolean onlyAvailable, String sort, int from, int size) {
        log.info("Getting public events. text: {}, categories: {}, paid: {}, rangeStart: {}, rangeEnd: {}, onlyAvailable: {}, sort: {}, from: {}, size: {}",
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
        if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
            log.error("rangeStart must be before rangeEnd. rangeStart: {}, rangeEnd: {}", rangeStart, rangeEnd);
            throw new BadRequestException("rangeStart must be before rangeEnd");
        }
        Specification<Event> spec = buildPublicSpec(text, categories, paid, rangeStart, rangeEnd, onlyAvailable);

        Sort sorting = Sort.unsorted();
        if ("EVENT_DATE".equals(sort)) {
            sorting = Sort.by(Sort.Direction.ASC, "eventDate");
        }

        List<EventShortDto> events = eventRepository.findAll(spec, PageRequest.of(from / size, size, sorting)).stream()
                .map(e -> EventMapper.toShortDto(e, getConfirmed(e.getId()), 0))
                .collect(Collectors.toList());
        log.info("Found {} public events", events.size());
        return events;
    }

    @Override
    public EventFullDto getPublicEvent(long eventId, String uri) {
        log.info("Getting public event with id: {}", eventId);
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> {
                    log.error("Event with id={} was not found", eventId);
                    return new NotFoundException("Event with id=" + eventId + " was not found");
                });
        if (event.getState() != Status.PUBLISHED) {
            log.error("Event with id={} is not published", eventId);
            throw new NotFoundException("Event with id=" + eventId + " was not found");
        }
        long views = getViews(uri);
        log.info("Found public event: {}, views: {}", event.getTitle(), views);
        return EventMapper.toFullDto(event, getConfirmed(eventId), views);
    }

    private long getViews(String uri) {
        try {
            Collection<EndpointStatDto> stats = statsClient.getStats(
                    LocalDateTime.of(2020, 1, 1, 0, 0),
                    LocalDateTime.now().plusMinutes(1),
                    List.of(uri),
                    true
            );
            return stats.stream()
                    .filter(s -> uri.equals(s.getUri()))
                    .mapToLong(EndpointStatDto::getHits)
                    .sum();
        } catch (Exception e) {
            log.warn("Failed to get views from stats service: {}", e.getMessage());
            return 0;
        }
    }

    private long getConfirmed(long eventId) {
        return requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
    }

    private void applyEventEdit(Event event, EventEditDto dto) {
        if (dto.getAnnotation() != null) event.setAnnotation(dto.getAnnotation());
        if (dto.getDescription() != null) event.setDescription(dto.getDescription());
        if (dto.getPaid() != null) event.setPaid(dto.getPaid());
        if (dto.getParticipantLimit() != null) event.setParticipantLimit(dto.getParticipantLimit());
        if (dto.getRequestModeration() != null) event.setRequestModeration(dto.getRequestModeration());
        if (dto.getTitle() != null) event.setTitle(dto.getTitle());
        if (dto.getLocation() != null) event.setLocation(dto.getLocation());
        if (dto.getCategory() != null) {
            Category cat = categoryRepository.findById(dto.getCategory())
                    .orElseThrow(() -> {
                        log.error("Category with id={} was not found", dto.getCategory());
                        return new NotFoundException("Category with id=" + dto.getCategory() + " was not found");
                    });
            event.setCategory(cat);
        }
    }

    private Specification<Event> buildAdminSpec(List<Long> users, List<String> states, List<Long> categories,
                                                LocalDateTime rangeStart, LocalDateTime rangeEnd) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (users != null && !users.isEmpty()) {
                predicates.add(root.get("initiator").get("id").in(users));
            }
            if (states != null && !states.isEmpty()) {
                List<Status> statusList = states.stream().map(Status::valueOf).collect(Collectors.toList());
                predicates.add(root.get("state").in(statusList));
            }
            if (categories != null && !categories.isEmpty()) {
                predicates.add(root.get("category").get("id").in(categories));
            }
            if (rangeStart != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("eventDate"),
                        rangeStart));
            }
            if (rangeEnd != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("eventDate"),
                        rangeEnd));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private Specification<Event> buildPublicSpec(String text, List<Long> categories, Boolean paid,
                                                 LocalDateTime rangeStart, LocalDateTime rangeEnd, boolean onlyAvailable) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("state"), Status.PUBLISHED));
            if (text != null && !text.isBlank()) {
                String pattern = "%" + text.toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("annotation")), pattern),
                        cb.like(cb.lower(root.get("description")), pattern)
                ));
            }
            if (categories != null && !categories.isEmpty()) {
                predicates.add(root.get("category").get("id").in(categories));
            }
            if (paid != null) {
                predicates.add(cb.equal(root.get("paid"), paid));
            }
            if (rangeStart != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("eventDate"),
                        rangeStart));
            } else {
                predicates.add(cb.greaterThan(root.get("eventDate"), LocalDateTime.now()));
            }
            if (rangeEnd != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("eventDate"),
                        rangeEnd));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
