package com.personnel_accounting.mapper;

import com.personnel_accounting.entity.domain.User;
import com.personnel_accounting.entity.dto.UserDTO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDTO userToUserDto(User user);
    User userDtoToUser(UserDTO userDTO);

    List<UserDTO> userListToUserDtoList(List<User> users);
    List<User> userDtoListToUserList(List<UserDTO> userDTOS);
}
