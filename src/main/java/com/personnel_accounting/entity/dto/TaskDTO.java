package com.personnel_accounting.entity.dto;

import com.personnel_accounting.entity.enums.TaskStatus;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class TaskDTO {
    private Long id;

    @NotBlank(message = "{task.validation.name.empty}")
    @Size(max = 100, message = "{task.validator.name.size}")
    private String name;

    @NotBlank(message = "{task.validator.description.empty}")
    private String description;
    private ProjectDTO project;
    private DepartmentDTO department;
    private EmployeeDTO reporter;
    private EmployeeDTO assignee;
    private TaskStatus status;
    private String createDate;
}
