package ru.mrstepan.ewmservice.service;

import org.springframework.stereotype.Service;
import ru.mrstepan.ewmservice.dto.CompilationDto;

import java.util.Collection;

@Service
public class CompilationServiceImpl implements CompilationService {
    @Override
    public Collection<CompilationDto> getCompilations(int from, boolean pinned, int size) {

    }

    @Override
    public CompilationDto getCompilationById(long id) {

    }
}
