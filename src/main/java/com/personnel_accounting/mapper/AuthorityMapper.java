package com.personnel_accounting.mapper;

import com.personnel_accounting.entity.domain.Authority;
import com.personnel_accounting.entity.dto.AuthorityDTO;
import org.mapstruct.Mapper;

import java.util.Set;

@Mapper(componentModel = "spring")
public interface AuthorityMapper {

    AuthorityDTO authorityToAuthorityDto(Authority authority);
    Authority authorityDtoToAuthority(AuthorityDTO authorityDTO);

    Set<AuthorityDTO> authoritySetToAuthorityDtoSet(Set<Authority> authorities);
    Set<Authority> authorityDtoSetToAuthoritySet(Set<AuthorityDTO> authorityDTOS);
}
