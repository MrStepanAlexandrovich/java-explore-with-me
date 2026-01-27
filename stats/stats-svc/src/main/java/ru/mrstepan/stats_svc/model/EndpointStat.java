package ru.mrstepan.stats_svc.model;

import lombok.Data;

@Data
public class EndpointStat {
    private String app;
    private String uri;
    private Integer hits;
}
