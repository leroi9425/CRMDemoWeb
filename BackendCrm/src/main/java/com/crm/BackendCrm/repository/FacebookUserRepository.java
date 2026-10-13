package com.crm.BackendCrm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.crm.BackendCrm.entity.FacebookUser;

public interface FacebookUserRepository extends JpaRepository<FacebookUser, Long> {
    // Define custom query methods if needed
    
}
