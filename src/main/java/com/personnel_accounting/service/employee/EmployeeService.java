package com.personnel_accounting.service.employee;

import com.personnel_accounting.entity.domain.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.sql.Time;
import java.util.List;

public interface EmployeeService {

    String editProfileImage(String id, User user);
    ReportCard trackTime(Task task, Time time);
    Task changeTaskStatus(Task task);
    Task addTaskInProject(Project project, Task task);
    Employee addProfileData(Employee employee, Profile profile);
    Employee editEmployeeName(Employee employee, String name);
    Profile findProfileByEmployee(Employee employee);

    List<Employee> findByNamePart(String namePart);
    List<Employee> findByPhonePart(String phonePart);
    List<Employee> findByEmailPart(String emailPart);

    Task findTask(Long id);
    Task saveTask(Task task);

    Employee find(Long id);
    Page<Employee> findAllEmployeeWithPaginationAndSearch(Pageable pageable, String search);
    Page<Employee> findAllActiveEmployeeWithPaginationAndSearch(Pageable pageable, String search);
    Page<Employee> findAllActiveAdminsWithPaginationAndSearch(Pageable pageable, String search);
    Page<Employee> findAllFreeAndActiveEmployeesWithPaginationAndSearch(Pageable pageable, String search);
    Page<Employee> findAllAssignedAndActiveEmployeesWithPaginationAndSearch(Pageable pageable, String search);
    Page<Employee> findAllDismissedEmployeeWithPaginationAndSearch(Pageable pageable, String search);
    Employee findByUser(User user);
    List<Employee> findByDepartment(Department department);
    Page<Employee> findEmployeesWithOpenProjectByDepartmentWithPaginationAndSearch
            (Department department, Pageable pageable, String search);
    Page<Employee> findEmployeesByDepartmentWithPaginationAndSearch(Department department, Pageable pageable, String search);
    Page<Employee> findEmployeesByProjectWithPaginationAndSearch(Project project, Pageable pageable, String search);

    Employee save(Employee employee);
    boolean removeWithInactivation(Employee employee);
    boolean remove(Employee employee);

    boolean inactivate(Employee employee);
    boolean activate(Employee employee);
}
