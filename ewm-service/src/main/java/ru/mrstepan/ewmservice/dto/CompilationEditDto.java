package ru.mrstepan.ewmservice.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;

@Data
public class CompilationEditDto {
    private Set<Long> events;
    private Boolean pinned;

    @Size(min = 1, max = 50)
    private String title;
}
