package ru.mrstepan.statsсlient;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import ru.mrstepan.statsdto.RequestInfoDto;
import ru.mrstepan.statsdto.EndpointStatDto;

import java.util.Collection;
import java.util.List;

@Service
public class StatsApiClientImpl {
    private final RestClient restClient;

    public StatsApiClientImpl(RestClient.Builder builder) {
        this.restClient = builder.baseUrl("https://localhost:9090")
                .build();
    }

    public void hit(RequestInfoDto requestInfoDto) {
        restClient.post()
                .uri("/hit")
                .body(requestInfoDto)
                .retrieve()
                .toBodilessEntity();
    }

    public Collection<EndpointStatDto> getStats(String start, String end, List<String> uris, Boolean unique) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("start", start)
                        .queryParam("end", end)
                        .queryParam("uris", uris)
                        .queryParam("unique", unique)
                        .path("/stats")
                        .build())
                .retrieve()
                .body(new ParameterizedTypeReference<Collection<EndpointStatDto>>() {});
    }
}
