package com.personnel_accounting.mapper;

import com.personnel_accounting.entity.domain.Department;
import com.personnel_accounting.entity.dto.DepartmentDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring",
        uses = DateMapper.class,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DepartmentMapper {

    DepartmentDTO toDto(Department department);

    @Mapping(target = "isActive", source = "active")
    Department toModal(DepartmentDTO departmentDTO);

    List<DepartmentDTO> toDtoList(List<Department> departments);
    List<Department> toModalList(List<DepartmentDTO> departmentDTOS);
}
