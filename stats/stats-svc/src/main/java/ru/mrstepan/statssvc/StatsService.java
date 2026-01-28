package ru.mrstepan.statssvc;

import ru.mrstepan.statsdto.EndpointStatDto;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface StatsService {
    void saveRequestInfo(ru.mrstepan.RequestInfoDto requestInfoDto);

    Collection<EndpointStatDto> getEndpointsStats(LocalDateTime start, LocalDateTime end, List<String> uris,
                                                  boolean unique);
}
