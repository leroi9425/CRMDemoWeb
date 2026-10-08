package com.crm.BackendCrm.repository;

import com.crm.BackendCrm.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    Optional<User> findByUsername(String username);
    java.util.List<User> findByRoles_Id(Long roleId);
    List<User> findByCompanyId(Long companyId); // JPA sẽ tự động tạo querry
}
