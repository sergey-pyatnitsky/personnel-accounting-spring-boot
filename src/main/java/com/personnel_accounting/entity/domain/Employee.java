package com.personnel_accounting.entity.domain;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "employee")
public class Employee extends BaseEntity {

    @OneToOne(
            cascade = {CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.MERGE, CascadeType.DETACH}
            , fetch = FetchType.LAZY)
    @JoinColumn(name = "username", unique = true, nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false)
    private Profile profile;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "active", columnDefinition = "boolean default false")
    private boolean isActive;

    @OneToMany(mappedBy = "employee")
    private List<EmployeePosition> employeePositionList;

    @OneToMany(mappedBy = "employee")
    private List<ReportCard> reportCardList;
}
