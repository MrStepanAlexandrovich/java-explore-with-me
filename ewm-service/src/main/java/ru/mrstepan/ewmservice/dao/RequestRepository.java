package ru.mrstepan.ewmservice.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mrstepan.ewmservice.model.Request;

public interface RequestRepository extends JpaRepository<Request, Long> {
}
