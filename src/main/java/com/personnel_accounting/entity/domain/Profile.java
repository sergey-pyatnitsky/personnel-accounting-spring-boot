package com.personnel_accounting.entity.domain;

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
@Table(name = "profile")
public class Profile extends BaseEntity {

    @Column(name = "education", length = 2048)
    private String education;

    @Column(name = "address")
    private String address;

    @Column(name = "phone", length = 60)
    private String phone;

    @Column(name = "email")
    private String email;

    @Column(name = "skills")
    @Type(type = "text")
    private String skills;

    @Column(name = "image_id", length = 45)
    private String imageId;

    @OneToOne(mappedBy = "profile")
    private Employee employee;
}
