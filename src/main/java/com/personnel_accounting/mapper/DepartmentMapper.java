package com.personnel_accounting.mapper;

import com.personnel_accounting.entity.domain.Department;
import com.personnel_accounting.entity.dto.DepartmentDTO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DepartmentMapper {

    DepartmentDTO departmentToDepartmentDto (Department department);
    Department DepartmentDtoToDepartment(DepartmentDTO departmentDTO);

    List<DepartmentDTO> departmentListToDepartmentDtoList(List<Department> departments);
    List<Department> departmentDtoListToDepartmentList(List<DepartmentDTO> departmentDTOS);
}
