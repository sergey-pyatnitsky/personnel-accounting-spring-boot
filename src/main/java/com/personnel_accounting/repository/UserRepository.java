package com.personnel_accounting.repository;

import com.personnel_accounting.entity.domain.Authority;
import com.personnel_accounting.entity.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    @Query
    List<User> findUsersByRolesContains(Authority authority);

    @Query
    boolean removeByUsername(String username);

    @Modifying
    @Query("update User u set u.isActive =:isActive where u.username =:username")
    int changeActiveStatus(@Param("username") String username,
                               @Param("isActive") boolean isActive);
}
