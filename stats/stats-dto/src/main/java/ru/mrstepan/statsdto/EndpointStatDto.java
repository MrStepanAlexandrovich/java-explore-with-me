package ru.mrstepan.statsdto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EndpointStatDto {
    private String app;
    private String uri;
    private Long hits;
}
