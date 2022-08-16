package com.personnel_accounting.mapper;

import com.personnel_accounting.entity.domain.Authority;
import com.personnel_accounting.entity.dto.AuthorityDTO;
import org.mapstruct.Mapper;

import java.util.Set;

@Mapper(componentModel = "spring")
public interface AuthorityMapper {

    AuthorityDTO toDto(Authority authority);
    Authority toModal(AuthorityDTO authorityDTO);

    Set<AuthorityDTO> toDtoList(Set<Authority> authorities);
    Set<Authority> toModalList(Set<AuthorityDTO> authorityDTOS);
}
