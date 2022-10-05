package com.personnel_accounting.entity.dto;

import lombok.Data;

import javax.validation.Valid;

@Data
public class UserDTO {
    private String username;
    private String password;

    @Valid
    private AuthorityDTO authority;
    private boolean isActive;
}
