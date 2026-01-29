package ru.mrstepan.statsdto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RequestInfoDto {
    @Max(255)
    private String app;
    private String uri;
    private String ip;
    @PastOrPresent
    private LocalDateTime timestamp;
}
