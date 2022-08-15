package com.personnel_accounting.entity.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.Set;

@Data
public class UserDTO {
    private String username;

    @NotBlank(message = "{user.validator.password.empty}")
    private String password;
    private Set<AuthorityDTO> roles;
    private boolean isActive;
}
