package com.personnel_accounting.mapper;

import com.personnel_accounting.entity.domain.Project;
import com.personnel_accounting.entity.dto.ProjectDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring",
        uses = {DepartmentMapper.class, DateMapper.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProjectMapper {

    ProjectDTO toDto(Project project);

    @Mapping(target = "isActive", source = "active")
    Project toModal(ProjectDTO projectDTO);

    List<ProjectDTO> toDtoList(List<Project> projectList);
    List<Project> toModalList(List<ProjectDTO> projectDTOS);
}
