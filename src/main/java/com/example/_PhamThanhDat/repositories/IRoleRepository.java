package com.example._PhamThanhDat.repositories;

import com.example._PhamThanhDat.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface IRoleRepository extends JpaRepository<Role, Long> {

    // Tìm kiếm Role theo ID (Dùng để gán quyền mặc định)
    @Query("SELECT r FROM Role r WHERE r.id = ?1")
    Role findRoleById(Long id);

    // Tìm kiếm Role theo tên (Ví dụ: "ADMIN", "USER")
    @Query("SELECT r FROM Role r WHERE r.name = ?1")
    Role findRoleByName(String name);
}