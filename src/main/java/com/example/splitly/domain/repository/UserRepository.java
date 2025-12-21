package com.example.splitly.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.splitly.domain.entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);

    Optional<User> findByResetToken(String token);


    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u WHERE LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "AND u.id != :currentUserId")
    List<User> searchByEmailKeyword(
            @Param("keyword") String keyword,
            @Param("currentUserId") Integer currentUserId
    );

    @Query("SELECT u FROM User u WHERE " +
            "(LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND u.id != :currentUserId")
    List<User> searchByEmailOrUsernameKeyword(
            @Param("keyword") String keyword,
            @Param("currentUserId") Integer currentUserId
    );
}
