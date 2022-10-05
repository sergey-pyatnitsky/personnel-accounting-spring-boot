package com.personnel_accounting.entity.dto;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class EmployeeDTO {
    private Long id;

    @Valid
    private UserDTO user;

    @NotBlank(message = "{employee.validator.name.empty}")
    @Size(max = 256, message = "{employee.validator.name.size}")
    private String name;

    @Valid
    private DepartmentDTO department;

    @Valid
    private ProfileDTO profile;
    private boolean isActive;
}
