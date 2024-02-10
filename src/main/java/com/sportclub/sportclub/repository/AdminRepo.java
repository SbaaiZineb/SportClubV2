package com.sportclub.sportclub.repository;

import com.sportclub.sportclub.entities.Coach;
import com.sportclub.sportclub.entities.Member;
import com.sportclub.sportclub.entities.Role;

import com.sportclub.sportclub.entities.UserApp;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.userdetails.User;

import java.util.List;

public interface AdminRepo extends JpaRepository<UserApp,Long> {
    Page<UserApp> findByNameContains(String mc, Pageable pageable);
    Page<UserApp> findByRolesRoleNameContainsOrRolesRoleNameContains(String role1,String role2,Pageable pageable);
    List<UserApp> findByNameContains(String name);

    //    u is an alias for UserApp.
    //    TYPE(u) = UserApp ensures that only instances of the UserApp class are retrieved.
    @Query("SELECT u FROM UserApp u WHERE u.cin LIKE %:cin% AND TYPE(u) = UserApp")
    List<UserApp> findByCinContainsIgnoreCase(@Param("cin") String cin);
    @Query("SELECT u FROM UserApp u WHERE u.tele LIKE %:tele% AND TYPE(u) = UserApp")
    List<UserApp> findByTeleContains(@Param("tele") String tele);

    List<UserApp> findByRolesRoleNameContains(String name);
    UserApp findByEmail(String email);
    @Query("select count(p) = 1 from UserApp p where p.email= ?1")
    Boolean findExistByEmail(String email);
    @Query("select count(p) = 1 from UserApp p where p.cin= ?1")
    Boolean findExistByCin(String cin);
    Page<UserApp> findByRolesRoleNameContains(String role, Pageable pageable);
    @Query("SELECT m FROM UserApp m JOIN m.roles r " +
            "WHERE r.roleName IN ('ADMIN', 'EMPLOYEE')" +
            "AND (lower(m.name) LIKE lower(concat('%', :keyword, '%')) " +
            "OR lower(m.lname) LIKE lower(concat('%', :keyword, '%')) " +
            "OR lower(m.tele) LIKE lower(concat('%', :keyword, '%')) " +
            "OR lower(m.cin) LIKE lower(concat('%', :keyword, '%')))")
    List<UserApp> findAdminByKeyword(@Param("keyword") String keyword);

}
