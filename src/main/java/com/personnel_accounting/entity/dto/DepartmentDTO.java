package com.personnel_accounting.entity.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class DepartmentDTO {
    private Long id;

    @NotBlank(message = "{department.validator.name.empty}")
    @Size(max = 256, message = "{department.validator.name.size}")
    private String name;
    private boolean isActive;
    private String startDate;
    private String createDate;
    private String endDate;
}
