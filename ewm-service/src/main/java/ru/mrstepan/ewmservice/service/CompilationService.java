package ru.mrstepan.ewmservice.service;

import ru.mrstepan.ewmservice.dto.CompilationDto;
import ru.mrstepan.ewmservice.dto.CompilationEditDto;
import ru.mrstepan.ewmservice.dto.NewCompilationDto;

import java.util.List;

public interface CompilationService {
    List<CompilationDto> getCompilations(Boolean pinned, int from, int size);

    CompilationDto getCompilationById(long id);

    CompilationDto addCompilation(NewCompilationDto dto);

    void deleteCompilation(long id);

    CompilationDto editCompilation(long id, CompilationEditDto dto);
}
