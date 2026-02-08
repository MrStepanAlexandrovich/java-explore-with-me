package ru.mrstepan.ewmservice.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Location {
    private float lat;
    private float lon;
}
