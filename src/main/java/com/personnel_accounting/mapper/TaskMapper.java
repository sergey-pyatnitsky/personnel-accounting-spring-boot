package com.personnel_accounting.mapper;

import com.personnel_accounting.entity.domain.Task;
import com.personnel_accounting.entity.dto.TaskDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring",
        uses = {EmployeeMapper.class, ProjectMapper.class, DepartmentMapper.class, DateMapper.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TaskMapper {

    @Mapping(target = "status", source = "taskStatus")
    TaskDTO toDto(Task task);

    @Mapping(target = "taskStatus", source = "status")
    Task toModal(TaskDTO taskDTO);

    List<TaskDTO> toDtoList(List<Task> tasks);
    List<Task> toModalList(List<TaskDTO> taskDTOS);
}
