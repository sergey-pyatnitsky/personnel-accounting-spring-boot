package com.personnel_accounting.mapper;

import com.personnel_accounting.entity.domain.Task;
import com.personnel_accounting.entity.dto.TaskDTO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TaskMapper {

    TaskDTO taskToTaskDto(Task task);
    Task taskDtoToTask(TaskDTO taskDTO);

    List<TaskDTO> taskListToTaskDtoList(List<Task> tasks);
    List<Task> taskDtoListToTaskList(List<TaskDTO> taskDTOS);
}
