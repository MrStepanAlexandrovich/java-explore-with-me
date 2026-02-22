package ru.mrstepan.ewmservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mrstepan.ewmservice.dao.CompilationRepository;
import ru.mrstepan.ewmservice.dao.EventRepository;
import ru.mrstepan.ewmservice.dao.RequestRepository;
import ru.mrstepan.ewmservice.dto.CompilationDto;
import ru.mrstepan.ewmservice.dto.CompilationEditDto;
import ru.mrstepan.ewmservice.dto.EventShortDto;
import ru.mrstepan.ewmservice.dto.NewCompilationDto;
import ru.mrstepan.ewmservice.exception.NotFoundException;
import ru.mrstepan.ewmservice.model.Compilation;
import ru.mrstepan.ewmservice.mapper.CompilationMapper;
import ru.mrstepan.ewmservice.model.Event;
import ru.mrstepan.ewmservice.mapper.EventMapper;
import ru.mrstepan.ewmservice.model.RequestStatus;
import ru.mrstepan.ewmservice.service.CompilationService;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;

    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, int from, int size) {
        log.info("Getting compilations. pinned: {}, from: {}, size: {}", pinned, from, size);
        List<Compilation> compilations;
        PageRequest page = PageRequest.of(from / size, size);
        if (pinned != null) {
            compilations = compilationRepository.findAllByPinned(pinned, page);
        } else {
            compilations = compilationRepository.findAll(page).getContent();
        }
        log.info("Found {} compilations", compilations.size());
        return compilations.stream()
                .map(c -> CompilationMapper.toDto(c, buildEventShortDtoMap(c)))
                .collect(Collectors.toList());
    }

    @Override
    public CompilationDto getCompilationById(long id) {
        log.info("Getting compilation by id: {}", id);
        Compilation compilation = compilationRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Compilation with id={} was not found", id);
                    return new NotFoundException("Compilation with id=" + id + " was not found");
                });
        log.info("Found compilation: {}", compilation.getTitle());
        return CompilationMapper.toDto(compilation, buildEventShortDtoMap(compilation));
    }

    @Override
    @Transactional
    public CompilationDto addCompilation(NewCompilationDto dto) {
        log.info("Adding compilation. Title: {}, pinned: {}", dto.getTitle(), dto.getPinned());
        Set<Event> events = new HashSet<>();
        if (dto.getEvents() != null && !dto.getEvents().isEmpty()) {
            events = new HashSet<>(eventRepository.findAllById(dto.getEvents()));
            log.info("Found {} events for compilation", events.size());
        }
        Compilation saved = compilationRepository.save(CompilationMapper.toCompilation(dto, events));
        log.info("Compilation saved. Id: {}", saved.getId());
        return CompilationMapper.toDto(saved, buildEventShortDtoMap(saved));
    }

    @Override
    public void deleteCompilation(long id) {
        log.info("Deleting compilation with id: {}", id);

        if (!compilationRepository.existsById(id)) {
            log.error("Compilation with id={} was not found", id);
            throw new NotFoundException("Compilation with id=" + id + " was not found");
        }

        compilationRepository.deleteById(id);
        log.info("Compilation with id: {} deleted", id);
    }

    @Override
    @Transactional
    public CompilationDto editCompilation(long id, CompilationEditDto dto) {
        log.info("Editing compilation with id: {}", id);

        Compilation compilation = compilationRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Compilation with id={} was not found", id);
                    return new NotFoundException("Compilation with id=" + id + " was not found");
                });
        if (dto.getTitle() != null) {
            log.info("Updating title to: {}", dto.getTitle());
            compilation.setTitle(dto.getTitle());
        }
        if (dto.getPinned() != null) {
            log.info("Updating pinned to: {}", dto.getPinned());
            compilation.setPinned(dto.getPinned());
        }
        if (dto.getEvents() != null) {
            log.info("Updating events list");
            compilation.setEvents(new HashSet<>(eventRepository.findAllById(dto.getEvents())));
        }
        Compilation saved = compilationRepository.save(compilation);
        log.info("Compilation with id: {} updated", saved.getId());
        return CompilationMapper.toDto(saved, buildEventShortDtoMap(saved));
    }

    private Map<Long, EventShortDto> buildEventShortDtoMap(Compilation compilation) {
        return compilation.getEvents().stream()
                .collect(Collectors.toMap(
                        Event::getId,
                        e -> EventMapper.toShortDto(e,
                                requestRepository.countByEventIdAndStatus(e.getId(), RequestStatus.CONFIRMED), 0)
                ));
    }
}
