package ru.mrstepan.statsdto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class GettingStatsParametersDto {
    @NotNull
    private LocalDateTime start;
    @NotNull
    private LocalDateTime end;
    private List<String> uris;
    private Boolean unique = false;
}
