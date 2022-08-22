package com.personnel_accounting.mapper;

import com.personnel_accounting.entity.domain.EmployeePosition;
import com.personnel_accounting.entity.dto.EmployeePositionDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring",
        uses = {ProjectMapper.class, EmployeeMapper.class, DepartmentMapper.class, DateMapper.class})
public interface EmployeePositionMapper {

    EmployeePositionDTO toDto(EmployeePosition employeePosition);

    @Mapping(target = "isActive", source = "active")
    EmployeePosition toModal(EmployeePositionDTO employeePositionDTO);

    List<EmployeePositionDTO> toDtoList(List<EmployeePosition> employeePositionList);
    List<EmployeePosition> toModalList(List<EmployeePositionDTO> employeePositionDTOS);
}
