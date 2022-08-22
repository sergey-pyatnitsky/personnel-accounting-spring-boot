package com.personnel_accounting.entity.domain;

import lombok.*;

import javax.persistence.*;
import java.sql.Date;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "project")
public class Project extends BaseEntity {

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "active", columnDefinition = "boolean default false")
    private boolean isActive;

    @Column(name = "start_date")
    private Date startDate;

    @Column(name = "end_date")
    private Date endDate;

    @ManyToOne
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @OneToMany(mappedBy = "project")
    private List<EmployeePosition> employeePositionList;

    @OneToMany(mappedBy = "project")
    private List<Task> taskList;
}
