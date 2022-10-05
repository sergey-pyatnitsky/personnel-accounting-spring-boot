package com.personnel_accounting.entity.dto;

import lombok.Data;

import javax.validation.Valid;

@Data
public class EmployeePositionDTO {
    private Long id;

    @Valid
    private EmployeeDTO employee;

    @Valid
    private ProjectDTO project;

    @Valid
    private DepartmentDTO department;

    @Valid
    private PositionDTO position;
    private boolean isActive;
    private String startDate;
    private String endDate;
}
