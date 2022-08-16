package com.personnel_accounting.mapper;

import com.personnel_accounting.entity.domain.Department;
import com.personnel_accounting.entity.dto.DepartmentDTO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DepartmentMapper {

    DepartmentDTO toDto (Department department);
    Department toModal(DepartmentDTO departmentDTO);

    List<DepartmentDTO> toDtoList(List<Department> departments);
    List<Department> toModalList(List<DepartmentDTO> departmentDTOS);
}
