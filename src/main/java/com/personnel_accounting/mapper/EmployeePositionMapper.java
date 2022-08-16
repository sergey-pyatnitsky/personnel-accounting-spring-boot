package com.personnel_accounting.mapper;

import com.personnel_accounting.entity.domain.EmployeePosition;
import com.personnel_accounting.entity.dto.EmployeePositionDTO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EmployeePositionMapper {

    EmployeePositionDTO toDto(EmployeePosition employeePosition);
    EmployeePosition toModal(EmployeePositionDTO employeePositionDTO);

    List<EmployeePositionDTO> toDtoList(List<EmployeePosition> employeePositionList);
    List<EmployeePosition> toModalList(List<EmployeePositionDTO> employeePositionDTOS);
}
