package com.crm.BackendCrm.entity;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// @Entity 
@AllArgsConstructor 
@NoArgsConstructor 
@Setter 
@Getter 
// @Table (name = "company_package")
public class CompanyPackage {
    // @ManyToOne 
    // @JoinColumn (name = "company_id", referencedColumnName = "id")
    private Company company;

    // @ManyToOne 
    // @JoinColumn (name = "package_id", referencedColumnName = "id")
    private Package pack;

    private LocalDate startDate;
    private LocalDate endDate;
}
