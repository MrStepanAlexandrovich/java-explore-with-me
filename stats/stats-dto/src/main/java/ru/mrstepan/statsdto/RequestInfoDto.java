package ru.mrstepan.statsdto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RequestInfoDto {
    @NotNull
    @Max(255)
    private String app;

    @NotNull
    @Max(2048)
    private String uri;

    @NotNull
    @Max(45)
    private String ip;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
}
