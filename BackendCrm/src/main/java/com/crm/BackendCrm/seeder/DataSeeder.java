package com.crm.BackendCrm.seeder;

import com.crm.BackendCrm.entity.*;
import com.crm.BackendCrm.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        seedPermissionsAndRoles();
        seedUsers();
        seedCustomers();
    }

    private void seedPermissionsAndRoles() {
        if (roleRepository.count() > 0) return;

        // 1. Tạo các Quyền
        Permission viewCust = permissionRepository.save(new Permission(null, "XEM_KHACH_HANG", "Xem danh sách khách hàng", null));
        Permission addCust = permissionRepository.save(new Permission(null, "THEM_KHACH_HANG", "Thêm khách hàng", null));
        Permission delCust = permissionRepository.save(new Permission(null, "XOA_KHACH_HANG", "Xóa khách hàng", null));
        Permission manageUser = permissionRepository.save(new Permission(null, "QUAN_LY_USER", "Quản lý hệ thống người dùng", null));

        // 2. Tạo Nhóm quyền (Role) và gán Quyền
        Role adminRole = new Role();
        adminRole.setName("ADMIN");
        adminRole.setPermissions(Set.of(viewCust, addCust, delCust, manageUser));
        roleRepository.save(adminRole);

        Role userRole = new Role();
        userRole.setName("USER");
        userRole.setPermissions(Set.of(viewCust)); // User thường chỉ được xem
        roleRepository.save(userRole);

        System.out.println("Đã seed Roles và Permissions");
    }

    private void seedUsers() {
        if (userRepository.count() > 0) return;

        Role adminRole = roleRepository.findByName("ADMIN").orElseThrow();
        Role userRole = roleRepository.findByName("USER").orElseThrow();

        User admin = new User();
        admin.setUsername("admin");
        admin.setEmail("admin@crmlite.com");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setFullName("Quản trị viên");
        admin.setRoles(Set.of(adminRole));
        userRepository.save(admin);

        User user1 = new User();
        user1.setUsername("nhanvien1");
        user1.setEmail("nhanvien1@crmlite.com");
        user1.setPassword(passwordEncoder.encode("user123"));
        user1.setFullName("Nguyễn Văn A");
        user1.setRoles(Set.of(userRole));
        userRepository.save(user1);

        User user2 = new User();
        user2.setUsername("nhanvien2");
        user2.setEmail("nhanvien2@crmlite.com");
        user2.setPassword(passwordEncoder.encode("user123"));
        user2.setFullName("Trần Thị B");
        user2.setRoles(Set.of(userRole));
        userRepository.save(user2);

        System.out.println("Đã seed 3 users: admin, nhanvien1, nhanvien2");
    }

    private void seedCustomers() {
        if (customerRepository.count() > 0) return;

        Customer c1 = new Customer();
        c1.setCustomerName("Lê Minh Kha");
        c1.setPhoneNumber("0901234567");
        c1.setEmail("kha.le@abc.com");
        c1.setLocation("Hà Nội");
        c1.setDateOfBirth("1990-01-01");
        c1.setGender(true);
        customerRepository.save(c1);

        Customer c2 = new Customer();
        c2.setCustomerName("Trần Ngọc Trinh");
        c2.setPhoneNumber("0912345678");
        c2.setEmail("trinh.tran@xyz.com");
        c2.setLocation("TP.HCM");
        c2.setDateOfBirth("1995-05-15");
        c2.setGender(false);
        customerRepository.save(c2);

        Customer c3 = new Customer();
        c3.setCustomerName("Phạm Nhật Vượng");
        c3.setPhoneNumber("0987654321");
        c3.setEmail("vuong.pham@gmail.com");
        c3.setLocation("Đà Nẵng");
        c3.setDateOfBirth("1988-08-08");
        c3.setGender(true);
        customerRepository.save(c3);

        System.out.println("Đã seed 3 customers");
    }
}
