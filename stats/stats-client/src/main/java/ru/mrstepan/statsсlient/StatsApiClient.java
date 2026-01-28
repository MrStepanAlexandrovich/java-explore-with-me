package ru.mrstepan.statsсlient;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.PostExchange;
import ru.mrstepan.RequestInfoDto;
import ru.mrstepan.statsdto.EndpointStatDto;
import java.util.Collection;
import java.util.List;

@Service
public interface StatsApiClient {

    @PostExchange("/hit")
    void hit(@RequestBody RequestInfoDto requestInfoDto);

    @GetExchange("/stats")
    Collection<EndpointStatDto> getStats(
            @RequestParam("start") String start,
            @RequestParam("end") String end,
            @RequestParam("uris") List<String> uris,
            @RequestParam("unique") Boolean unique
    );
}
