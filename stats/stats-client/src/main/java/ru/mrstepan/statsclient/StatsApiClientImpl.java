package ru.mrstepan.statsclient;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import ru.mrstepan.statsdto.GettingStatsParametersDto;
import ru.mrstepan.statsdto.EndpointStatDto;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class StatsApiClientImpl {
    private final RestClient restClient;

    public StatsApiClientImpl(RestClient.Builder builder,
                              @Value("${stats.service.url}") String statsServiceUrl) {
        this.restClient = builder.baseUrl(statsServiceUrl)
                .build();
    }

    public void hit(HttpServletRequest request) throws IOException {
        BufferedReader reader = request.getReader();
        String body = reader.lines().collect(Collectors.joining());

        restClient.post()
                .uri(request.getRequestURI())
                .body(body)
                .retrieve()
                .toBodilessEntity();
    }

    public Collection<EndpointStatDto> getStats(@Valid GettingStatsParametersDto gettingStatsDto) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("start", gettingStatsDto.getStart())
                        .queryParam("end", gettingStatsDto.getEnd())
                        .queryParam("uris", gettingStatsDto.getUris())
                        .queryParam("unique", gettingStatsDto.getUnique())
                        .path("/stats")
                        .build())
                .retrieve()
                .body(new ParameterizedTypeReference<Collection<EndpointStatDto>>() {});
    }
}
