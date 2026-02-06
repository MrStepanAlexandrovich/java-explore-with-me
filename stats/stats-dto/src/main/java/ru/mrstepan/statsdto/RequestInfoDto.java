package ru.mrstepan.statsdto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RequestInfoDto {
    @NotNull
    @Size(max = 255)
    private String app;

    @NotNull
    @Size(max = 2048)
    private String uri;

    @NotNull
    @Size(max = 45)
    private String ip;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
}
