package com.crm.BackendCrm.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.crm.BackendCrm.entity.PushSubscription;

public interface PushSubscriptionRepository extends JpaRepository<PushSubscription, Long> {
        List<PushSubscription> findByUser_Id(Long userId);
}