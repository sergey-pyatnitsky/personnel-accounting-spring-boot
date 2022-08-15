package com.personnel_accounting.entity.domain;

import lombok.*;

import javax.persistence.*;
import java.sql.Date;
import java.sql.Time;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "report_card")
public class ReportCard extends BaseEntity {

    @Column(name = "date", nullable = false)
    private Date date;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(name = "working_time", nullable = false)
    private Time workingTime;
}
