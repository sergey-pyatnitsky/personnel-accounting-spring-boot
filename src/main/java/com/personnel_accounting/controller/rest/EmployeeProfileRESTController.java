package com.personnel_accounting.controller.rest;

import com.personnel_accounting.entity.domain.Employee;
import com.personnel_accounting.entity.domain.User;
import com.personnel_accounting.entity.dto.EmployeeDTO;
import com.personnel_accounting.entity.dto.UserDTO;
import com.personnel_accounting.entity.enums.Role;
import com.personnel_accounting.exception.IncorrectDataException;
import com.personnel_accounting.mapper.EmployeeMapper;
import com.personnel_accounting.mapper.ProfileMapper;
import com.personnel_accounting.mapper.UserMapper;
import com.personnel_accounting.service.employee.EmployeeService;
import com.personnel_accounting.service.user.UserService;
import com.personnel_accounting.utils.AuthenticationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api")
public class EmployeeProfileRESTController {

    @Autowired
    private EmployeeMapper employeeMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ProfileMapper profileMapper;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private UserService userService;

    @Autowired
    private MessageSource messageSource;

    @PostMapping("/employee/profile/check_old_password")
    public ResponseEntity<?> checkOldUserPass(@Valid @RequestBody UserDTO userDTO, Authentication authentication) {
        User user = userService.find(AuthenticationUtil.getUsernameFromAuthentication(authentication));
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        if (!passwordEncoder.matches(userDTO.getPassword(), user.getPassword().replace("{bcrypt}", "")))
            throw new IncorrectDataException(
                    messageSource.getMessage("user.error.password", null, LocaleContextHolder.getLocale()));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/employee/profile/edit_password")
    public ResponseEntity<?> editUserPass(@Valid @RequestBody UserDTO userDTO, Authentication authentication) {
        User user = userService.find(AuthenticationUtil.getUsernameFromAuthentication(authentication));
        userService.changeAuthData(user, userDTO.getPassword());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/employee/profile/get_profile_data")
    public ResponseEntity<?> getProfileData(Authentication authentication) {
        User user = userService.find(AuthenticationUtil.getUsernameFromAuthentication(authentication));
        if (!userService.getMaxRole(user).equals(Role.SUPER_ADMIN)) {
            Employee employee = employeeService.findByUser(user);
            EmployeeDTO employeeDTO = employeeMapper.toDto(employee);
            employeeDTO.setProfile(profileMapper.toDto(employee.getProfile()));
            employeeDTO.setUser(userMapper.toDto(user));
            return new ResponseEntity<>(employeeDTO, HttpStatus.OK);
        }
        return ResponseEntity.ok(userMapper.toDto(user));
    }

    @PostMapping("/employee/profile/edit")
    public ResponseEntity<?> editProfileData(@Valid @RequestBody EmployeeDTO employeeDTO, Authentication authentication) {
        Employee employee = employeeService.findByUser(
                userService.find(AuthenticationUtil.getUsernameFromAuthentication(authentication)));
        employee.setName(employeeDTO.getName());
        employeeService.addProfileData(employee, profileMapper.toModal(employeeDTO.getProfile()));
        employeeService.save(employee);
        return ResponseEntity.ok().build();
    }
}
