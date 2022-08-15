package com.personnel_accounting.entity.dto;

import lombok.Data;

import javax.validation.constraints.Size;

@Data
public class ProfileDTO {
    private Long id;

    @Size(max = 2048, message = "{profile.validator.education.size}")
    private String education;

    @Size(max = 256, message = "{profile.validator.address.size}")
    private String address;

    @Size(max = 60, message = "{profile.validator.phone.size}")
    private String phone;

    @Size(max = 256, message = "{profile.validator.email.size}")
    private String email;
    private String skills;
    private String imageId;
}
