package com.personnel_accounting.service.employee;

import com.personnel_accounting.entity.domain.*;
import com.personnel_accounting.entity.enums.Role;
import com.personnel_accounting.entity.enums.TaskStatus;
import com.personnel_accounting.repository.*;
import com.personnel_accounting.utils.ValidationUtil;
import com.personnel_accounting.validation.EmployeeValidator;
import com.personnel_accounting.validation.ProfileValidator;
import com.personnel_accounting.validation.TaskValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Date;
import java.sql.Time;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private AuthorityRepository authorityRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ReportCardRepository reportCardRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmployeePositionRepository employeePositionRepository;

    @Autowired
    private EmployeeValidator employeeValidator;

    @Autowired
    private TaskValidator taskValidator;

    @Autowired
    private ProfileValidator profileValidator;

    @Override
    public String editProfileImage(String id, User user) {
        Profile profile = employeeRepository.findByUser(user).getProfile();
        profile.setImageId(id);
        return profileRepository.save(profile).getImageId();
    }

    @Override
    public ReportCard trackTime(Task task, Time time) {
        Date date = new Date(System.currentTimeMillis());
        return reportCardRepository.save(
                ReportCard.builder()
                        .date(date)
                        .task(task)
                        .employee(task.getAssignee())
                        .workingTime(time)
                        .createDate(date).build());
    }

    @Override
    public Task changeTaskStatus(Task task) {
        task.setTaskStatus(TaskStatus.values()[task.getTaskStatus().ordinal() + 1]);
        task.setModifiedDate(new Date(System.currentTimeMillis()));
        return taskRepository.save(task);
    }

    @Override
    public Task addTaskInProject(Project project, Task task) {
        task.setProject(project);
        task.setTaskStatus(TaskStatus.OPEN);
        task.setCreateDate(new Date(System.currentTimeMillis()));

        ValidationUtil.validate(task, taskValidator);
        return taskRepository.save(task);
    }

    @Override
    public Employee addProfileData(Employee employee, Profile profile) {
        if (employee == null) return null;
        employee.setProfile(profileRepository.findProfileByEmployee(employee));
        employee.getProfile().setSkills(profile.getSkills());
        employee.getProfile().setEmail(profile.getEmail());
        employee.getProfile().setPhone(profile.getPhone());
        employee.getProfile().setAddress(profile.getAddress());
        employee.getProfile().setEducation(profile.getEducation());
        ValidationUtil.validate(employee.getProfile(), profileValidator);
        return employeeRepository.save(employee);
    }

    @Override
    public Employee editEmployeeName(Employee employee, String name) {
        employee.setName(name);
        employee.setModifiedDate(new Date(System.currentTimeMillis()));

        ValidationUtil.validate(employee, employeeValidator);
        return employeeRepository.save(employee);
    }

    @Override
    public Profile findProfileByEmployee(Employee employee) {
        employee = employeeRepository.findById(employee.getId()).orElse(null);
        return employee != null ? profileRepository.findById(employee.getProfile().getId()).orElse(null) : null;
    }

    @Override
    public List<Employee> findByNamePart(String namePart) {
        return employeeRepository.findEmployeesByNameContaining(namePart.trim());
    }

    @Override
    public List<Employee> findByPhonePart(String phonePart) {
        return profileRepository.findProfilesByPhoneContaining(phonePart.trim()).stream()
                .map(employeeRepository::findByProfile).collect(Collectors.toList());
    }

    @Override
    public List<Employee> findByEmailPart(String emailPart) {
        return profileRepository.findProfilesByEmailContaining(emailPart.trim()).stream()
                .map(employeeRepository::findByProfile).collect(Collectors.toList());
    }

    @Override
    public Task findTask(Long id) {
        return taskRepository.findById(id).orElse(null);
    }

    @Override
    public Task saveTask(Task task) {
        ValidationUtil.validate(task, taskValidator);
        task.setModifiedDate(new Date(System.currentTimeMillis()));
        return taskRepository.save(task);
    }

    @Override
    public Employee find(Long id) {
        Employee employee = employeeRepository.findById(id).orElse(null);
        if (employee != null) employee.getUser();
        return employee;
    }

    @Override
    public Page<Employee> findAllEmployeeWithPaginationAndSearch(Pageable pageable, String search) {
        return employeeRepository.findAll(pageable, search);
    }

    @Override
    public Page<Employee> findAllActiveEmployeeWithPaginationAndSearch(Pageable pageable, String search) {
        return employeeRepository.findEmployeesByUser_ActiveIsTrueAndUser_RolesIn(
                Arrays.asList(
                        authorityRepository.findByName(Role.EMPLOYEE.name()).orElse(null),
                        authorityRepository.findByName(Role.DEPARTMENT_HEAD.name()).orElse(null),
                        authorityRepository.findByName(Role.PROJECT_MANAGER.name()).orElse(null)
                ),
                pageable, search);
    }

    @Override
    public Page<Employee> findAllActiveAdminsWithPaginationAndSearch(Pageable pageable, String search) {
        return employeeRepository.findEmployeesByUser_ActiveIsTrueAndUser_RolesIn(
                Collections.singletonList(authorityRepository.findByName(Role.ADMIN.name()).orElse(null)),
                pageable, search);
    }

    @Override
    public Page<Employee> findAllFreeAndActiveEmployeesWithPaginationAndSearch(Pageable pageable, String search) {
        return employeeRepository.findEmployeesByDepartmentIsNullAndUser_ActiveIsTrue(pageable, search);
    }

    @Override
    public Page<Employee> findAllAssignedAndActiveEmployeesWithPaginationAndSearch(Pageable pageable, String search) {
        return employeeRepository.findEmployeesByDepartmentIsNotNullAndUser_ActiveIsTrue(pageable, search);
    }

    @Override
    public Page<Employee> findAllDismissedEmployeeWithPaginationAndSearch(Pageable pageable, String search) {
        return employeeRepository.findByUser_IsActiveFalseAndUser_RolesIn(
                Arrays.asList(
                        authorityRepository.findByName(Role.EMPLOYEE.name()).orElse(null),
                        authorityRepository.findByName(Role.DEPARTMENT_HEAD.name()).orElse(null),
                        authorityRepository.findByName(Role.PROJECT_MANAGER.name()).orElse(null),
                        authorityRepository.findByName(Role.ADMIN.name()).orElse(null)
                ),
                pageable, search);
    }

    @Override
    public Employee findByUser(User user) {
        return employeeRepository.findByUser(user);
    }

    @Override
    public List<Employee> findByDepartment(Department department) {
        return employeeRepository.findByDepartment(department);
    }

    @Override
    public Page<Employee> findEmployeesWithOpenProjectByDepartmentWithPaginationAndSearch(
            Department department, Pageable pageable, String search) {
        return employeeRepository.findEmployeesByDepartmentWithOpenProjects(department, pageable, search);
    }

    @Override
    public Page<Employee> findEmployeesByDepartmentWithPaginationAndSearch(Department department, Pageable pageable, String search) {
        return employeeRepository.findEmployeesByDepartment(department, pageable, search);
    }

    @Override
    public Page<Employee> findEmployeesByProjectWithPaginationAndSearch(Project project, Pageable pageable, String search) {
        return employeeRepository.findEmployeesByProject(project, pageable, search);
    }

    @Override
    public Employee save(Employee employee) {
        ValidationUtil.validate(employee, employeeValidator);
        return employeeRepository.save(employee);
    }

    @Override
    public boolean removeWithInactivation(Employee employee) {
        if (inactivate(employee)) {
            return userRepository.changeActiveStatus(employee.getUser().getUsername(), false) == 1;
        }
        return false;
    }

    @Override
    public boolean remove(Employee employee) {
        return employeeRepository.removeById(employee.getId());
    }

    @Override
    public boolean inactivate(Employee employee) {
        Date date = new Date(System.currentTimeMillis());
        employeePositionRepository.findByEmployee(employee).forEach(employeePosition -> {
            employeePosition.setModifiedDate(date);
            employeePosition.setActive(false);
            employeePositionRepository.save(employeePosition);
        });
        List<Task> tasks = taskRepository.findTasksByAssignee(employee);
        tasks.stream().filter(task -> task.getTaskStatus() == TaskStatus.IN_PROGRESS).collect(Collectors.toList())
                .forEach(task -> {
                    task.setModifiedDate(date);
                    task.setTaskStatus(TaskStatus.OPEN);
                    taskRepository.save(task);
                });
        tasks.stream().filter(task -> task.getTaskStatus() == TaskStatus.DONE).collect(Collectors.toList())
                .forEach(task -> {
                    task.setModifiedDate(date);
                    task.setTaskStatus(TaskStatus.CLOSED);
                    taskRepository.save(task);
                });
        return employeeRepository.changeActiveStatus(employee.getId(), false, new Date(System.currentTimeMillis())) == 1;
    }

    @Override
    public boolean activate(Employee employee) {
        employeePositionRepository.findByEmployee(employee).forEach(employeePosition -> {
            employeePosition.setModifiedDate(new Date(System.currentTimeMillis()));
            employeePosition.setActive(true);
            employeePositionRepository.save(employeePosition);
        });
        return employeeRepository.changeActiveStatus(employee.getId(), true, new Date(System.currentTimeMillis())) == 1;
    }
}
