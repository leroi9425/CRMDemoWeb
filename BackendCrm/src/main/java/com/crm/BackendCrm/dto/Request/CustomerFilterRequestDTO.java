package com.crm.BackendCrm.dto.Request;

public record CustomerFilterRequestDTO(
    String search,
    Boolean gender,
    String customerName,
    String location,
    String phoneNumber,
    String customerCode,

    String fromDateOfBirth,
    String toDateOfBirth,

    String sortField,
    String sortDirection
) {}
