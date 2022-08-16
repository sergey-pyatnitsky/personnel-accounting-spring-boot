package com.personnel_accounting.mapper;

import com.personnel_accounting.entity.domain.Project;
import com.personnel_accounting.entity.dto.ProjectDTO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

    ProjectDTO toDto(Project project);
    Project toModal(ProjectDTO projectDTO);

    List<ProjectDTO> toDtoList(List<Project> projectList);
    List<Project> toModalList(List<ProjectDTO> projectDTOS);
}
