package com.personnel_accounting.mapper;

import com.personnel_accounting.entity.domain.Project;
import com.personnel_accounting.entity.dto.ProjectDTO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

    ProjectDTO projectDtoToProject(Project project);
    Project projectDtoToProject(ProjectDTO projectDTO);

    List<ProjectDTO> projectListToProjectDtoList(List<Project> projectList);
    List<Project> projectDtoListToProjectList(List<ProjectDTO> projectDTOS);
}
