package ru.mrstepan.ewmservice.service;

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
import ru.mrstepan.ewmservice.model.CompilationMapper;
import ru.mrstepan.ewmservice.model.Event;
import ru.mrstepan.ewmservice.model.EventMapper;
import ru.mrstepan.ewmservice.model.RequestStatus;

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
        List<Compilation> compilations;
        PageRequest page = PageRequest.of(from / size, size);
        if (pinned != null) {
            compilations = compilationRepository.findAllByPinned(pinned);
        } else {
            compilations = compilationRepository.findAll();
        }
        return compilations.stream()
                .map(c -> CompilationMapper.toDto(c, buildEventShortDtoMap(c)))
                .collect(Collectors.toList());
    }

    @Override
    public CompilationDto getCompilationById(long id) {
        Compilation compilation = compilationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Compilation with id=" + id + " was not found"));
        return CompilationMapper.toDto(compilation, buildEventShortDtoMap(compilation));
    }

    @Override
    @Transactional
    public CompilationDto addCompilation(NewCompilationDto dto) {
        Set<Event> events = new HashSet<>();
        if (dto.getEvents() != null && !dto.getEvents().isEmpty()) {
            events = new HashSet<>(eventRepository.findAllById(dto.getEvents()));
        }
        Compilation saved = compilationRepository.save(CompilationMapper.toCompilation(dto, events));
        return CompilationMapper.toDto(saved, buildEventShortDtoMap(saved));
    }

    @Override
    public void deleteCompilation(long id) {
        if (!compilationRepository.existsById(id)) {
            throw new NotFoundException("Compilation with id=" + id + " was not found");
        }
        compilationRepository.deleteById(id);
    }

    @Override
    @Transactional
    public CompilationDto editCompilation(long id, CompilationEditDto dto) {
        Compilation compilation = compilationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Compilation with id=" + id + " was not found"));
        if (dto.getTitle() != null) compilation.setTitle(dto.getTitle());
        if (dto.getPinned() != null) compilation.setPinned(dto.getPinned());
        if (dto.getEvents() != null) {
            compilation.setEvents(new HashSet<>(eventRepository.findAllById(dto.getEvents())));
        }
        Compilation saved = compilationRepository.save(compilation);
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
