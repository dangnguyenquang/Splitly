package com.example.splitly.domain.repository;

import com.example.splitly.domain.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Integer> {
    List<Permission> findAllByPermissionIdIn(Set<String> permissions);
}
