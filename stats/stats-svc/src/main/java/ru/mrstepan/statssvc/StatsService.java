package ru.mrstepan.statssvc;

import ru.mrstepan.statsdto.EndpointStatDto;
import ru.mrstepan.statsdto.RequestInfoDto;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface StatsService {
    void saveRequestInfo(RequestInfoDto requestInfoDto);

    Collection<EndpointStatDto> getEndpointsStats(LocalDateTime start, LocalDateTime end, List<String> uris,
                                                  boolean unique);
}
