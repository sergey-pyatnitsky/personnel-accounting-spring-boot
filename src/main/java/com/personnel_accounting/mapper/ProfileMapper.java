package com.personnel_accounting.mapper;

import com.personnel_accounting.entity.domain.Profile;
import com.personnel_accounting.entity.dto.ProfileDTO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProfileMapper {

    ProfileDTO toDto(Profile profile);
    Profile toModal(ProfileDTO profileDTO);

    List<ProfileDTO> toDtoList(List<Profile> profileList);
    List<Profile> toModalList(List<ProfileDTO> profileDTOS);
}
