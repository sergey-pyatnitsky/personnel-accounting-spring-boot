package com.personnel_accounting.mapper;

import com.personnel_accounting.entity.domain.Task;
import com.personnel_accounting.entity.dto.TaskDTO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TaskMapper {

    TaskDTO toDto(Task task);
    Task toModal(TaskDTO taskDTO);

    List<TaskDTO> toDtoList(List<Task> tasks);
    List<Task> toModalList(List<TaskDTO> taskDTOS);
}
