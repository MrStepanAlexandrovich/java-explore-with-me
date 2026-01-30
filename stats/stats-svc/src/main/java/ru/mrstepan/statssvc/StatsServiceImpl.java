package ru.mrstepan.statssvc;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.mrstepan.statsdto.EndpointStatDto;
import ru.mrstepan.statsdto.RequestInfoDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatsServiceImpl implements StatsService {
    private final StatsRepository statsRepository;

    @Override
    public void saveRequestInfo(RequestInfoDto requestInfoDto) {
        log.info("Saving request. app: {}, uri: {}, ip: {}, timestamp: {}",
                requestInfoDto.getApp(), requestInfoDto.getUri(), requestInfoDto.getIp(), requestInfoDto.getTimestamp());
        statsRepository.save(RequestInfoMapper.toRequestInfo(requestInfoDto));
    }

    @Override
    public Collection<EndpointStatDto> getEndpointsStats(
            String start, String end, List<String> uris, boolean unique
    ) {
        LocalDateTime start1 = LocalDateTime.parse(start, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        LocalDateTime end1 = LocalDateTime.parse(end, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        log.info("Start getting endpoints stats from {} to {} for uris: {}. Unique ips: {}",
                start1, end1, uris, unique);
        Collection<EndpointStatDto> endpointStats;
        if (unique) {
            log.trace("Getting stats with unique ips");
            endpointStats = statsRepository.getStatsWithUniqueIps(start1, end1, uris);
        } else {
            log.trace("Getting stats");
            endpointStats = statsRepository.getStats(start1, end1, uris);
        }

        log.info("Got stats for {} endpoints", endpointStats.size());
        return endpointStats;
    }
}
