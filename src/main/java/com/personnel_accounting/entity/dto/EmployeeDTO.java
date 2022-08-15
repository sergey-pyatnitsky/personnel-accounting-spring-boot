package com.personnel_accounting.entity.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class EmployeeDTO {
    private Long id;
    private UserDTO user;

    @NotBlank(message = "{employee.validator.name.empty}")
    @Size(max = 256, message = "{employee.validator.name.size}")
    private String name;
    private DepartmentDTO department;
    private ProfileDTO profile;
    private boolean isActive;
}
