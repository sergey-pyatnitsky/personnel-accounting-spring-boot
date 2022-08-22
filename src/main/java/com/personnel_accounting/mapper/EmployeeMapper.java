package com.personnel_accounting.mapper;

import com.personnel_accounting.entity.domain.Employee;
import com.personnel_accounting.entity.dto.EmployeeDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring",
        uses = {DepartmentMapper.class, UserMapper.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EmployeeMapper {

    EmployeeDTO toDto(Employee employee);

    @Mapping(target = "isActive", source = "active")
    Employee toModal(EmployeeDTO employeeDTO);

    List<EmployeeDTO> toDtoList(List<Employee> employees);
    List<Employee> toModalList(List<EmployeeDTO> employeeDTOS);
}
