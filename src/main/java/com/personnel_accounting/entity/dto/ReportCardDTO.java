package com.personnel_accounting.entity.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.sql.Date;
import java.sql.Time;

@Data
public class ReportCardDTO {
    private Long id;
    private Date date;
    private TaskDTO task;
    private EmployeeDTO employee;

    @NotEmpty(message = "{report_card.validation.workingTime.empty}")
    private Time workingTime;
}
