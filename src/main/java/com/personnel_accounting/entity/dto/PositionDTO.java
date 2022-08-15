package com.personnel_accounting.entity.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class PositionDTO {
    private Long id;

    @NotBlank(message = "{position.validator.name.empty}")
    @Size(max = 40, message = "{position.validator.name.size}")
    private String name;
}
