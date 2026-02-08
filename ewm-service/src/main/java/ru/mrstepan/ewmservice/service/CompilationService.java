package ru.mrstepan.ewmservice.service;

import ru.mrstepan.ewmservice.dto.CompilationDto;
import ru.mrstepan.ewmservice.dto.CompilationEditDto;

import java.util.Collection;

public interface CompilationService {
    Collection<CompilationDto> getCompilations(int from, boolean pinned, int size);

    CompilationDto getCompilationById(long id);

    void addCompilation(CompilationDto compilationDto);

    void deleteCompilation(long id);

    void editCompilation(long id, CompilationEditDto compilationEditDto);
}
