package ru.mrstepan.ewmservice.service;

public interface CompilationService {
    void getCompilations(int from, boolean pinned, int size);

    void getCompilationsById(long id);
}
