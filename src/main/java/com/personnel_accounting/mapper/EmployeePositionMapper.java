package com.personnel_accounting.mapper;

import com.personnel_accounting.entity.domain.EmployeePosition;
import com.personnel_accounting.entity.dto.EmployeePositionDTO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EmployeePositionMapper {

    EmployeePositionDTO employeePositionToEmployeePositionDto(EmployeePosition employeePosition);
    EmployeePosition employeePositionDtoToEmployeePosition(EmployeePositionDTO employeePositionDTO);

    List<EmployeePositionDTO> employeePositionListToEmployeePositionDtoList(List<EmployeePosition> employeePositionList);
    List<EmployeePosition> employeePositionDtoListToEmployeePositionList(List<EmployeePositionDTO> employeePositionDTOS);
}
