package com.example.splitly.domain.repository;

import com.example.splitly.domain.entity.Permission;
import com.example.splitly.domain.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
}
