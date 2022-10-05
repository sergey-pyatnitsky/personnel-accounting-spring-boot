package com.personnel_accounting.repository;

import com.personnel_accounting.entity.domain.Employee;
import com.personnel_accounting.entity.domain.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {

    List<Profile> findProfilesByPhoneContaining(String phone);
    List<Profile> findProfilesByEmailContaining(String email);
    Profile findProfileByEmployee(Employee employee);
}