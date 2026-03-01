package ru.mrstepan.ewmservice.dao;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.mrstepan.ewmservice.model.Compilation;

import java.util.List;

public interface CompilationRepository extends JpaRepository<Compilation, Long> {
    List<Compilation> findAllByPinned(Boolean pinned);

    List<Compilation> findAllByPinned(Boolean pinned, Pageable pageable);

    List<Compilation> findAll();
}
