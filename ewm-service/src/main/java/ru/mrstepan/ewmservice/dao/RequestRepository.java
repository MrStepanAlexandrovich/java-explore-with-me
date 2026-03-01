package ru.mrstepan.ewmservice.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.mrstepan.ewmservice.model.Request;
import ru.mrstepan.ewmservice.model.RequestStatus;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findAllByRequester_Id(long userId);

    List<Request> findAllByEvent_Id(long eventId);

    List<Request> findAllByIdIn(Collection<Long> ids);

    @Query("SELECT COUNT(r) FROM Request r WHERE r.event.id = :eventId AND r.status = :status")
    long countByEventIdAndStatus(long eventId, RequestStatus status);

    Optional<Request> findByRequester_IdAndEvent_Id(long userId, long eventId);

    List<Request> findAllByEvent_IdAndStatus(long eventId, RequestStatus status);
}
