package com.crm.BackendCrm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.crm.BackendCrm.entity.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
}
