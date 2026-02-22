package ru.mrstepan.statsclient;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import ru.mrstepan.statsdto.GettingStatsParametersDto;
import ru.mrstepan.statsdto.EndpointStatDto;
import ru.mrstepan.statsdto.RequestInfoDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;

@Service
public class StatsApiClientImpl {
    private final RestClient restClient;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public StatsApiClientImpl(RestClient.Builder builder,
                              @Value("${stats.service.url}") String statsServiceUrl) {
        this.restClient = builder.baseUrl(statsServiceUrl)
                .build();
    }

    public void hit(String app, String uri, String ip) {
        RequestInfoDto requestInfo = new RequestInfoDto();
        requestInfo.setApp(app);
        requestInfo.setUri(uri);
        requestInfo.setIp(ip);
        requestInfo.setTimestamp(LocalDateTime.now());

        restClient.post()
                .uri("/hit")
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestInfo)
                .retrieve()
                .toBodilessEntity();
    }

    public Collection<EndpointStatDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/stats")
                        .queryParam("start", start.format(FORMATTER))
                        .queryParam("end", end.format(FORMATTER))
                        .queryParam("uris", uris)
                        .queryParam("unique", unique)
                        .build())
                .retrieve()
                .body(new ParameterizedTypeReference<Collection<EndpointStatDto>>() {});
    }

    public Collection<EndpointStatDto> getStats(@Valid GettingStatsParametersDto gettingStatsDto) {
        return getStats(gettingStatsDto.getStart(), gettingStatsDto.getEnd(),
                gettingStatsDto.getUris(), gettingStatsDto.getUnique());
    }
}
