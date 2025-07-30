package com.example.splitly.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.splitly.domain.entity.GroupInfo;

@Repository
public interface GroupInfoRepository extends JpaRepository<GroupInfo, Long>{
    
}
