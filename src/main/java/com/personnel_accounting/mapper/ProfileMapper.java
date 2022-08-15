package com.personnel_accounting.mapper;

import com.personnel_accounting.entity.domain.Profile;
import com.personnel_accounting.entity.dto.ProfileDTO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProfileMapper {

    ProfileDTO profileToProfileDto(Profile profile);
    Profile profileDtoToProfile(ProfileDTO profileDTO);

    List<ProfileDTO> profileListToProfileDtoList(List<Profile> profileList);
    List<Profile> profileDtoListToProfileList(List<ProfileDTO> profileDTOS);
}
