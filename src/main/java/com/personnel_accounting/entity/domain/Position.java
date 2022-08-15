package com.personnel_accounting.entity.domain;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "position")
public class Position extends BaseEntity {

    @Column(name = "name", length = 40)
    private String name;
}
