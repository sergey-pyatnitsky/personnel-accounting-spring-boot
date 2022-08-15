package com.personnel_accounting.mapper;

import com.personnel_accounting.entity.domain.Employee;
import com.personnel_accounting.entity.dto.EmployeeDTO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {

    EmployeeDTO employeeToEmployeeDto(Employee employee);
    Employee employeeDtoToEmployee(EmployeeDTO employeeDTO);

    List<EmployeeDTO> employeeListToEmployeeDtoList(List<Employee> employees);
    List<Employee> employeeDtoListToEmployeeList(List<EmployeeDTO> employeeDTOS);
}
