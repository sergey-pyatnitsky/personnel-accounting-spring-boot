package com.personnel_accounting.entity.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class ProjectDTO {
    private Long id;

    @NotBlank(message = "{project.validator.name.empty}")
    @Size(max = 256, message = "{project.validator.name.size}")
    private String name;
    private DepartmentDTO department;
    private boolean isActive;
    private String createDate;
    private String endDate;
}
