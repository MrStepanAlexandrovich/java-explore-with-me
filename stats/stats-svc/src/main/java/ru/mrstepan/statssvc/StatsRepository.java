package ru.mrstepan.statssvc;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.mrstepan.statsdto.EndpointStatDto;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Repository
public interface StatsRepository extends JpaRepository<RequestInfo, Long> {
    @Query("""
            SELECT NEW ru.mrstepan.stats_svc.EndpointStatDto(e.app, e.uri, COUNT(*)) 
            FROM RequestInfo e 
            WHERE e.timestamp BETWEEN :start AND :end
            AND e.uri IN :uris
            GROUP BY e.app, e.uri
            """)
    Collection<EndpointStatDto> getStats(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("uris") List<String> uris
    );

    @Query("SELECT NEW ru.mrstepan.stats_svc.EndpointStatDto(e.app, e.uri, COUNT(DISTINCT e.ip)) " +
            "FROM RequestInfo e " +
            "WHERE e.timestamp BETWEEN :start AND :end " +
            "AND e.uri IN :uris " +
            "GROUP BY e.app, e.uri")
    Collection<EndpointStatDto> getStatsWithUniqueIps(
            LocalDateTime start,
            LocalDateTime end,
            List<String> uris
    );
}
