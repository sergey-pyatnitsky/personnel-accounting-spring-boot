package com.personnel_accounting.entity.domain;

import com.personnel_accounting.entity.enums.TaskStatus;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Type;

import javax.persistence.*;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "task")
public class Task extends BaseEntity {

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "description")
    @Type(type = "text")
    private String description;

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @OneToOne
    @JoinColumn(name = "reporter_id", nullable = false)
    private Employee reporter;

    @OneToOne
    @JoinColumn(name = "assignee_id", nullable = false)
    private Employee assignee;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 15)
    private TaskStatus taskStatus;

    @OneToOne(mappedBy = "task")
    private ReportCard reportCard;
}
