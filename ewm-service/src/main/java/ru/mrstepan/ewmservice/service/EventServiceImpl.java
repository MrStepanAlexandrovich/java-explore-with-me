package ru.mrstepan.ewmservice.service;

import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
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
import ru.mrstepan.ewmservice.exception.ConflictException;
import ru.mrstepan.ewmservice.exception.NotFoundException;
import ru.mrstepan.ewmservice.model.Category;
import ru.mrstepan.ewmservice.model.Event;
import ru.mrstepan.ewmservice.model.EventMapper;
import ru.mrstepan.ewmservice.model.Request;
import ru.mrstepan.ewmservice.model.RequestMapper;
import ru.mrstepan.ewmservice.model.RequestStatus;
import ru.mrstepan.ewmservice.model.Status;
import ru.mrstepan.ewmservice.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final RequestRepository requestRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public List<EventShortDto> getUserEvents(long userId, int from, int size) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));
        return eventRepository.findAllByInitiator_Id(userId).stream()
                .map(e -> EventMapper.toShortDto(e, getConfirmed(e.getId()), 0))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventFullDto addEvent(NewEventDto dto, long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));
        Category category = categoryRepository.findById(dto.getCategory())
                .orElseThrow(() -> new NotFoundException("Category with id=" + dto.getCategory() + " was not found"));

        if (dto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ConflictException("Field: eventDate. Error: должно содержать дату, которая еще не наступила. Value: " + dto.getEventDate());
        }

        Event event = EventMapper.toEvent(dto, category, user);
        return EventMapper.toFullDto(eventRepository.save(event), 0, 0);
    }

    @Override
    public EventFullDto getUserEvent(long eventId, long userId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
        if (!event.getInitiator().getId().equals(userId)) {
            throw new NotFoundException("Event with id=" + eventId + " was not found");
        }
        return EventMapper.toFullDto(event, getConfirmed(eventId), 0);
    }

    @Override
    @Transactional
    public EventFullDto updateUserEvent(long userId, long eventId, EventEditDto dto) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
        if (!event.getInitiator().getId().equals(userId)) {
            throw new NotFoundException("Event with id=" + eventId + " was not found");
        }
        if (event.getState() == Status.PUBLISHED) {
            throw new ConflictException("Only pending or canceled events can be changed");
        }
        if (dto.getEventDate() != null) {
            if (dto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
                throw new ConflictException("Field: eventDate. Error: must be in future. Value: " + dto.getEventDate());
            }
            event.setEventDate(dto.getEventDate());
        }
        applyEventEdit(event, dto);
        if ("SEND_TO_REVIEW".equals(dto.getStateAction())) {
            event.setState(Status.PENDING);
        } else if ("CANCEL_REVIEW".equals(dto.getStateAction())) {
            event.setState(Status.CANCELED);
        }
        return EventMapper.toFullDto(eventRepository.save(event), getConfirmed(eventId), 0);
    }

    @Override
    public List<RequestDto> getEventRequests(long userId, long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
        if (!event.getInitiator().getId().equals(userId)) {
            throw new NotFoundException("Event with id=" + eventId + " was not found");
        }
        return requestRepository.findAllByEvent_Id(eventId).stream()
                .map(RequestMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult changeRequestStatus(long userId, long eventId,
                                                              EventRequestStatusUpdateRequest request) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
        if (!event.getInitiator().getId().equals(userId)) {
            throw new NotFoundException("Event with id=" + eventId + " was not found");
        }

        List<Request> requests = requestRepository.findAllByIdIn(request.getRequestIds());
        RequestStatus newStatus = RequestStatus.valueOf(request.getStatus());

        for (Request req : requests) {
            if (req.getStatus() != RequestStatus.PENDING) {
                throw new ConflictException("Request must have status PENDING");
            }
        }

        if (newStatus == RequestStatus.CONFIRMED) {
            int limit = event.getParticipantLimit() != null ? event.getParticipantLimit() : 0;
            long confirmed = getConfirmed(eventId);
            if (limit > 0 && confirmed >= limit) {
                throw new ConflictException("The participant limit has been reached");
            }
            List<RequestDto> confirmedList = new ArrayList<>();
            List<RequestDto> rejectedList = new ArrayList<>();
            for (Request req : requests) {
                if (limit == 0 || confirmed < limit) {
                    req.setStatus(RequestStatus.CONFIRMED);
                    confirmedList.add(RequestMapper.toDto(requestRepository.save(req)));
                    confirmed++;
                } else {
                    req.setStatus(RequestStatus.REJECTED);
                    rejectedList.add(RequestMapper.toDto(requestRepository.save(req)));
                }
            }
            return new EventRequestStatusUpdateResult(confirmedList, rejectedList);
        } else {
            List<RequestDto> rejectedList = requests.stream()
                    .peek(r -> r.setStatus(RequestStatus.REJECTED))
                    .map(r -> RequestMapper.toDto(requestRepository.save(r)))
                    .collect(Collectors.toList());
            return new EventRequestStatusUpdateResult(List.of(), rejectedList);
        }
    }

    @Override
    public List<EventFullDto> getAdminEvents(List<Long> users, List<String> states, List<Long> categories,
                                             LocalDateTime rangeStart, LocalDateTime rangeEnd, int from, int size) {
        Specification<Event> spec = buildAdminSpec(users, states, categories, rangeStart, rangeEnd);
        return eventRepository.findAll(spec).stream()
                .map(e -> EventMapper.toFullDto(e, getConfirmed(e.getId()), 0))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventFullDto updateAdminEvent(long eventId, EventEditDto dto) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));

        if ("PUBLISH_EVENT".equals(dto.getStateAction())) {
            if (event.getState() != Status.PENDING) {
                throw new ConflictException("Cannot publish the event because it's not in the right state: " + event.getState());
            }
            LocalDateTime checkDate = dto.getEventDate() != null
                    ? dto.getEventDate() : event.getEventDate();
            if (checkDate.isBefore(LocalDateTime.now().plusHours(1))) {
                throw new ConflictException("Cannot publish: event date must be at least 1 hour from now");
            }
            event.setState(Status.PUBLISHED);
            event.setPublishedOn(LocalDateTime.now());
        } else if ("REJECT_EVENT".equals(dto.getStateAction())) {
            if (event.getState() == Status.PUBLISHED) {
                throw new ConflictException("Cannot reject published event");
            }
            event.setState(Status.CANCELED);
        }

        if (dto.getEventDate() != null) {
            event.setEventDate(dto.getEventDate());
        }

        applyEventEdit(event, dto);
        return EventMapper.toFullDto(eventRepository.save(event), getConfirmed(eventId), 0);
    }

    @Override
    public List<EventShortDto> getPublicEvents(String text, List<Long> categories, Boolean paid,
                                               LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                               boolean onlyAvailable, String sort, int from, int size) {
        Specification<Event> spec = buildPublicSpec(text, categories, paid, rangeStart, rangeEnd, onlyAvailable);
        return eventRepository.findAll(spec, PageRequest.of(from / size, size)).stream()
                .map(e -> EventMapper.toShortDto(e, getConfirmed(e.getId()), 0))
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto getPublicEvent(long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
        if (event.getState() != Status.PUBLISHED) {
            throw new NotFoundException("Event with id=" + eventId + " was not found");
        }
        return EventMapper.toFullDto(event, getConfirmed(eventId), 0);
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
                    .orElseThrow(() -> new NotFoundException("Category with id=" + dto.getCategory() + " was not found"));
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
