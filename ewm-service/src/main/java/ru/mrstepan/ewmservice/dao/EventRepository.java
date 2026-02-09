package ru.mrstepan.ewmservice.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mrstepan.ewmservice.model.Event;

public interface EventRepository extends JpaRepository<Event, Long> {
}
