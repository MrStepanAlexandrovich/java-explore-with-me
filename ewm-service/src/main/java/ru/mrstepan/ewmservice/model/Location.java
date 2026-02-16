package ru.mrstepan.ewmservice.model;

import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
public class Location {
    private float lat;
    private float lon;
}
