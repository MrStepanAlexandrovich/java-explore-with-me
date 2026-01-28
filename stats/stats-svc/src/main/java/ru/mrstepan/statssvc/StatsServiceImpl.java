package ru.mrstepan.statssvc;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.mrstepan.statsdto.EndpointStatDto;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatsServiceImpl implements StatsService {
    private final StatsRepository statsRepository;

    @Override
    public void saveRequestInfo(ru.mrstepan.RequestInfoDto requestInfoDto) {
        log.info("Saving request. app: {}, uri: {}, ip: {}, timestamp: {}",
                requestInfoDto.getApp(), requestInfoDto.getUri(), requestInfoDto.getIp(), requestInfoDto.getTimestamp());
        statsRepository.save(RequestInfoMapper.toRequestInfo(requestInfoDto));
    }

    @Override
    public Collection<EndpointStatDto> getEndpointsStats(
            LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique
    ) {
        log.info("Start getting endpoints stats from {} to {} for uris: {}. Unique ips: {}",
                start, end, uris, unique);
        Collection<EndpointStatDto> endpointStats;
        if (unique) {
            log.trace("Getting stats with unique ips");
            endpointStats = statsRepository.getStatsWithUniqueIps(start, end, uris);
        } else {
            log.trace("Getting stats");
            endpointStats = statsRepository.getStats(start, end, uris);
        }

        log.info("Got stats for {} endpoints", endpointStats.size());
        return endpointStats;
    }
}
