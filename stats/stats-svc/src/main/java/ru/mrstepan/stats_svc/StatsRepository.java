package ru.mrstepan.stats_svc;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.mrstepan.stats_svc.model.EndpointStat;
import ru.mrstepan.stats_svc.model.RequestInfo;

import java.time.LocalDateTime;
import java.util.Collection;

@Repository
public interface StatsRepository extends JpaRepository<RequestInfo, Long> {
    Collection<EndpointStat> getStats(
            LocalDateTime start,
            LocalDateTime end,
            Collection<String> uris
    );

    Collection<EndpointStat> getStatsWithUniqueIps(
            LocalDateTime start,
            LocalDateTime end,
            Collection<String> uris
    );
}
