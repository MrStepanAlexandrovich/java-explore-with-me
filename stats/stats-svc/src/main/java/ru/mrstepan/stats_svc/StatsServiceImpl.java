package ru.mrstepan.stats_svc;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mrstepan.stats_svc.model.EndpointStat;
import ru.mrstepan.stats_svc.model.RequestInfoMapper;

import java.time.LocalDateTime;
import java.util.Collection;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {
    private final StatsRepository statsRepository;

    @Override
    public void saveRequestInfo(ru.mrstepan.RequestInfoDto requestInfoDto) {
        statsRepository.save(RequestInfoMapper.toRequestInfo(requestInfoDto));
    }

    @Override
    public Collection<EndpointStat> getEndpointsStats(
            LocalDateTime start, LocalDateTime end, Collection<String> uris, boolean unique
    ) {
        Collection<EndpointStat> endpointStats;
        if (unique) {
            endpointStats = statsRepository.getStatsWithUniqueIps(start, end, uris);
        } else {
            endpointStats = statsRepository.getStats(start, end, uris);
        }

        return endpointStats;
    }
}
