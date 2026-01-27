package ru.mrstepan.stats_svc;

import ru.mrstepan.stats_svc.model.EndpointStat;

import java.time.LocalDateTime;
import java.util.Collection;

public interface StatsService {
    void saveRequestInfo(ru.mrstepan.RequestInfoDto requestInfoDto);

    Collection<EndpointStat> getEndpointsStats(LocalDateTime start, LocalDateTime end, Collection<String> uris, boolean unique);
}
