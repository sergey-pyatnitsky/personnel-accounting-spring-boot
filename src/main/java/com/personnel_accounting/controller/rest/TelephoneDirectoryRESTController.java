package com.personnel_accounting.controller.rest;

import com.personnel_accounting.entity.dto.EmployeeDTO;
import com.personnel_accounting.mapper.EmployeeMapper;
import com.personnel_accounting.mapper.ProfileMapper;
import com.personnel_accounting.service.employee.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class TelephoneDirectoryRESTController {

    @Autowired
    private EmployeeMapper employeeMapper;

    @Autowired
    private ProfileMapper profileMapper;

    @Autowired
    private EmployeeService employeeService;

    @PostMapping("/api/employee/telephone-directory/search/by_name/{name}")
    public ResponseEntity<?> getSearchResultByName(@PathVariable String name) {
        List<EmployeeDTO> employees = employeeService.findByNamePart(name).stream()
                .map(employee -> {
                    EmployeeDTO tempEmployee = employeeMapper.toDto(employee);
                    tempEmployee.setProfile(profileMapper.toDto(employeeService.findProfileByEmployee(employee)));
                    return tempEmployee;
                }).collect(Collectors.toList());
        return !employees.isEmpty()
                ? ResponseEntity.ok(employees)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @PostMapping("/api/employee/telephone-directory/search/by_phone/{phone}")
    public ResponseEntity<?> getSearchResultByPhone(@PathVariable String phone) {
        List<EmployeeDTO> employees = employeeService.findByPhonePart(phone).stream()
                .map(employee -> {
                    EmployeeDTO tempEmployee = employeeMapper.toDto(employee);
                    tempEmployee.setProfile(profileMapper.toDto(employeeService.findProfileByEmployee(employee)));
                    return tempEmployee;
                }).collect(Collectors.toList());
        return !employees.isEmpty()
                ? ResponseEntity.ok(employees)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @PostMapping("/api/employee/telephone-directory/search/by_email/{email}")
    public ResponseEntity<?> getSearchResultByEmail(@PathVariable String email) {
        List<EmployeeDTO> employees = employeeService.findByEmailPart(email).stream()
                .map(employee -> {
                    EmployeeDTO tempEmployee = employeeMapper.toDto(employee);
                    tempEmployee.setProfile(profileMapper.toDto(employeeService.findProfileByEmployee(employee)));
                    return tempEmployee;
                }).collect(Collectors.toList());
        return !employees.isEmpty()
                ? ResponseEntity.ok(employees)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
