package com.personnel_accounting.controller.rest;

import com.personnel_accounting.entity.domain.User;
import com.personnel_accounting.service.employee.EmployeeService;
import com.personnel_accounting.service.google.drive.FileManager;
import com.personnel_accounting.service.user.UserService;
import com.personnel_accounting.utils.AuthenticationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.GeneralSecurityException;

@RestController
@RequestMapping("/api")
public class ImageController {

    @Autowired
    private UserService userService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private FileManager fileManager;

    @PostMapping("/uploadFile")
    public ResponseEntity<?> uploadFile(@RequestParam(name = "file", required = false) MultipartFile file,
                                        Authentication authentication) {
        User user = userService.find(AuthenticationUtil.getUsernameFromAuthentication(authentication));
        String imageId = employeeService.editProfileImage(
                fileManager.uploadFile(file, user.getUsername() + "/boot-profile.png"), user);
        return ResponseEntity.ok(imageId);
    }

    @GetMapping("/downloadFile/{id}")
    public void downloadFile(@PathVariable String id, HttpServletResponse response) throws IOException, GeneralSecurityException {
        fileManager.downloadFile(id, response.getOutputStream());
    }
}
