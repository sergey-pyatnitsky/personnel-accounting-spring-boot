package com.personnel_accounting.mapper;

import com.personnel_accounting.entity.domain.Employee;
import com.personnel_accounting.entity.dto.EmployeeDTO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {

    EmployeeDTO toDto(Employee employee);
    Employee toModal(EmployeeDTO employeeDTO);

    List<EmployeeDTO> toDtoList(List<Employee> employees);
    List<Employee> toModalList(List<EmployeeDTO> employeeDTOS);
}
