package ru.mrstepan.stats_svc;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mrstepan.stats_svc.model.EndpointStat;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class StatsController {
    private final StatsService statsService;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void hit(@RequestBody @Valid ru.mrstepan.RequestInfoDto requestInfoDto) {
        statsService.saveRequestInfo(requestInfoDto);
    }

    @GetMapping("/stats")
    public ResponseEntity<Collection<EndpointStat>> getStats(
            @RequestParam LocalDateTime start,
            @RequestParam LocalDateTime end,
            @RequestParam List<String> uris,
            @RequestParam(defaultValue = "false") Boolean unique
    ) {
        Collection<EndpointStat> endpointStats = statsService.getEndpointsStats(start, end, uris, unique);

        return new ResponseEntity<>(endpointStats, HttpStatus.OK);
    }
}
