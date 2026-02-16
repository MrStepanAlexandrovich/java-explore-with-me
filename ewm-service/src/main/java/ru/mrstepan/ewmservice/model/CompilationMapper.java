package ru.mrstepan.ewmservice.model;

import ru.mrstepan.ewmservice.dto.CompilationDto;
import ru.mrstepan.ewmservice.dto.EventShortDto;
import ru.mrstepan.ewmservice.dto.NewCompilationDto;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class CompilationMapper {
    public static Compilation toCompilation(NewCompilationDto dto, Set<Event> events) {
        Compilation compilation = new Compilation();
        compilation.setTitle(dto.getTitle());
        compilation.setPinned(dto.getPinned() != null ? dto.getPinned() : false);
        compilation.setEvents(events);
        return compilation;
    }

    public static CompilationDto toDto(Compilation compilation, Map<Long, EventShortDto> eventShortDtoMap) {
        CompilationDto dto = new CompilationDto();
        dto.setId(compilation.getId());
        dto.setTitle(compilation.getTitle());
        dto.setPinned(compilation.getPinned());
        Set<EventShortDto> eventDtos = compilation.getEvents().stream()
                .map(e -> eventShortDtoMap.getOrDefault(e.getId(), EventMapper.toShortDto(e, 0, 0)))
                .collect(Collectors.toSet());
        dto.setEvents(eventDtos);
        return dto;
    }
}
