package ru.mrstepan.ewmservice.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mrstepan.ewmservice.model.Compilation;

public interface CompilationRepository extends JpaRepository<Compilation, Long> {
}
