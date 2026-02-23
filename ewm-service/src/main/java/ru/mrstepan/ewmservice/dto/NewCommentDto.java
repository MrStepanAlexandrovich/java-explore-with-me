package ru.mrstepan.ewmservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class NewCommentDto {
    @NotBlank
    @Size(max = 2200)
    private String text;
}
