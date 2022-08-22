package com.personnel_accounting.mapper;

import com.personnel_accounting.entity.domain.User;
import com.personnel_accounting.entity.dto.UserDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    UserDTO toDto(User user);

    @Mapping(target = "isActive", source = "active")
    User toModal(UserDTO userDTO);

    List<UserDTO> toDtoList(List<User> users);
    List<User> toModalList(List<UserDTO> userDTOS);
}
