package com.crm.BackendCrm.specification;

import java.time.LocalDate;

import org.springframework.data.jpa.domain.Specification;

import com.crm.BackendCrm.entity.Customer;

public class CustomerSpecification {
    public static Specification<Customer> hasSearch(String seachString){
        return (root, query, cb) -> {
            String keySearch = "%" + seachString.toLowerCase() + "%";
            System.out.println(
                seachString.codePoints()
                    .mapToObj(Integer::toHexString)
                    .toList()
            );
            System.out.println(
                keySearch.codePoints()
                    .mapToObj(Integer::toHexString)
                    .toList()
            );
            System.out.println("KEY: " + keySearch);
            return cb.or(
                cb.like(
                    cb.lower(root.get("customerName")),
                    keySearch
                ),
                cb.like(
                    cb.lower(root.get("location")),
                    keySearch
                ),
                cb.like(
                    cb.lower(root.get("dateOfBirth")),
                    keySearch
                ),
                cb.like(
                    cb.lower(root.get("phoneNumber")),
                    keySearch
                )
            );
        };            
    }
    public static Specification<Customer> hasUserId(Long userId){
        System.out.println("user id khi loc ne: " + userId);
        return (root, query, cb) ->
                cb.equal(
                    root.get("user").get("id"), 
                    userId
                );
    }
    public static Specification<Customer> hasCustomerName(String name){
        return (root, query, cb) ->
            cb.like(
                root.get("customerName"),        // tên properties của Object ở đây là customer
                    "%" + name + "%"             // tham số cần so sánh
            );
    }
    public static Specification<Customer> hasLocation(String location){
        return (root, query, cb) ->
            cb.like(
                root.get("location"),
                "%" + location + "%"
            );
    }
    public static Specification<Customer> hasPhoneNumber(String phoneNumber) {
        return (root, query, cb) ->
                cb.like(
                    root.get("phoneNumber"),
                    "%" + phoneNumber + "%"
                );
    }
    public static Specification<Customer> hasCustomerCode(String customerCode) {

        return (root, query, cb) ->
                cb.like(
                    root.get("customerCode"),
                    "%" + customerCode + "%"
                );
    }
    public static Specification<Customer> dateOfBirthFrom(String date){
        return (root, query, cb) -> 
            cb.greaterThan(
                root.get("dateOfBirth"), 
                date
            );
    }  
    public static Specification<Customer> dateOfBirthTo(String date) {
        return (root, query, cb) ->
                cb.lessThanOrEqualTo(
                    root.get("dateOfBirth"),
                    date
                );
    }
    public static Specification<Customer> hasGender(boolean gender) {
        return (root, query, cb) ->
                cb.equal(
                    root.get("gender"),
                    gender
                );
    }

    public static Specification<Customer> createdAtFrom(LocalDate fromDate) {

        return (root, query, cb) ->
                cb.greaterThanOrEqualTo(
                    root.get("createdAt"),
                    fromDate.atStartOfDay()
                );
    }

    public static Specification<Customer> createdAtTo(LocalDate toDate) {

        return (root, query, cb) ->
                cb.lessThan(
                    root.get("createdAt"),
                    toDate.plusDays(1).atStartOfDay()
                );
    }
}
