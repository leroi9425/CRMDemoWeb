package com.crm.BackendCrm.repository;

import com.crm.BackendCrm.entity.Contact;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ContactRepository extends JpaRepository<Contact, Long> {
    List<Contact> getByCustomerCode(String customerCode);
}
